package scripts.gui

import compuquest.clienting.gui.*
import compuquest.simulation.characters.*
import compuquest.simulation.input.Commands
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.capitalize
import silentorb.mythic.ent.emptyId
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.isButtonJustReleased
import silentorb.mythic.localization.Text
import java.lang.Integer.min

typealias AccessoryList = List<Pair<Any, AccessoryDefinition>>

@RegisterClass
class AccessoriesBrowser : Control(), HasOnClose, CustomInputHandler {

	var accessoryLists: List<AccessoryList> = listOf()
	var proficiencies: ProficiencyLevels = mapOf()
	var columnNodes: List<List<Control>> = listOf()
	var maxTransfers: Int = 0
	var actor: Id = emptyId
	var selectedItem: Any? = null
	val itemLookup: MutableMap<Int, AccessoryUiItem> = mutableMapOf()
	var slotLimits: Map<AccessorySlot, Int> = mapOf()
	var isEditing: Boolean = false

	val slots = listOf(
		AccessorySlot.primary,
		AccessorySlot.utility,
		AccessorySlot.mobility,
		AccessorySlot.passive,
		AccessorySlot.consumable,
	)

	fun setLabel(key: String, text: String) {
		val label = (findNode(key) as? Label)
		label?.text = text
	}

	fun setLabel(key: String, text: String, isVisible: Boolean = true) {
		val label = (findNode(key) as? Label)
		label?.visible = isVisible
		if (isVisible) {
			label?.text = text
		}
	}

	fun setLabel(key: String, text: Text) =
		setLabel(key, resolveText(text))

	fun updateLeftListEnabled() {
		if (isEditing) {
			val rightList = accessoryLists[1]
			val underMaxTransfers = maxTransfers == 0 || rightList.size < maxTransfers

			val column = columnNodes[0]
			for (list in column) {
				for (i in 0 until list.getChildCount()) {
					val item = list.getChild(i) as AccessoryUiItem
					val slot = item.accessory!!.slot
					val slotLimit = slotLimits.getOrDefault(slot, -1)
					val enabled = underMaxTransfers && (slotLimit == -1 || rightList.count { it.second.slot == slot } < slotLimit)
					item.disabled = !enabled
				}
			}
		}
	}

	fun onItemSelected(item: AccessoryUiItem) {
		val accessory = item.accessory!!

		setLabel("name", accessory.name)
		setLabel("description", accessory.description)
		(findNode("slot") as? Button)?.text = camelCaseToTitle(accessory.slot.name)
		val proficienciesList = findNode("proficiencies")!!
		clearChildren(proficienciesList)

		for (proficiency in getProficiences(accessory)) {
			val label = Button()
			label.text = camelCaseToTitle(proficiency)
			label.disabled = !proficiencies.containsKey(proficiency)
			label.sizeFlagsHorizontal = 0
			proficienciesList.addChild(label)
		}

		val coolDownLabel = (findNode("cooldown") as? Label)
		val showCooldown = accessory.cooldown != 0f
		if (showCooldown) {
			val cooldown = accessory.cooldown.toString().replace(".0", "")
			val suffix = if (accessory.cooldown == 1f)
				"second"
			else
				"seconds"

			coolDownLabel?.text = "$cooldown $suffix"
		}
		coolDownLabel?.visible = showCooldown
		(findNode("cooldown-label") as? Label)?.visible = showCooldown
		selectedItem = item.key
	}

	@RegisterFunction
	fun on_item_selected(id: Int) {
		val item = itemLookup[id]!!
		onItemSelected(item)
	}

	fun getItemHierarchy(item: AccessoryUiItem): Pair<Int, Int> {
		val parent = item.getParent()
		columnNodes.forEachIndexed { columnIndex, column ->
			column.forEachIndexed { listIndex, list ->
				if (list == parent) {
					return columnIndex to listIndex
				}
			}
		}

		return -1 to -1
	}

	fun updateSlotPanelVisibility() {
		for (column in columnNodes) {
			for (list in column) {
				val panel = list.getParent() as Control
				panel.visible = list.getChildCount() > 0
			}
		}
	}

	fun updateAccessoryLists() {
		accessoryLists = columnNodes
			.map { column ->
				column.flatMap { list ->
					list.getChildren().filterIsInstance<AccessoryUiItem>()
						.map { it.key to it.accessory!! }
				}
			}
	}

	fun getItemByHierarchyIndices(columnIndex: Int, rowIndex: Int): AccessoryUiItem? {
		val column = columnNodes[columnIndex]
		var i = 0
		for (list in column) {
			for (itemIndex in (0 until list.getChildCount())) {
				if (i++ == rowIndex) {
					return list.getChild(itemIndex) as? AccessoryUiItem
				}
			}
		}

		return null
	}

	fun onItemActivated(item: AccessoryUiItem) {
		if (item.key != selectedItem) {
			onItemSelected(item)
			return
		}
		val (columnIndex, listIndex) = getItemHierarchy(item)
		if (columnIndex == -1) return
		val otherColumn = columnNodes[1 - columnIndex]
		val otherListNode = otherColumn[listIndex]
		item.getParent()!!.removeChild(item)
		val index = accessoryLists[columnIndex].indexOfFirst { it.first == item.key }
		otherListNode.addChild(item)
		updateAccessoryLists()
		updateSlotPanelVisibility()
		updateLeftListEnabled()

		val newSelectedIndex = min(index, accessoryLists[columnIndex].size - 1)
		if (newSelectedIndex > -1) {
			getItemByHierarchyIndices(columnIndex, newSelectedIndex)?.grabFocus()
		} else {
			item.grabFocus() // This seems like a Godot bug but focus is lost when the focused item changes parents
		}
	}

	@RegisterFunction
	fun on_item_activated(id: Int) {
		val item = itemLookup[id]!!
		onItemActivated(item)
	}

	fun populateList(list: Control, accessories: AccessoryList) {
		accessories.forEach { (key, accessory) ->
//			list.addItem(resolveText(accessory.name))
			val button = AccessoryUiItem()
			button.key = key
			button.accessory = accessory
			button.text = resolveText(accessory.name)
			button.name = "button"
//			button.disabled = !(enabled == null || enabled(context, null))
			val id = button.getInstanceId().toInt()
			if (isEditing) {
				button.connect("focus_entered", this, "on_item_selected", variantArrayOf(id))
				button.connect("pressed", this, "on_item_activated", variantArrayOf(id))
			}
			list.addChild(button)
			itemLookup[id] = button
		}
	}

	@RegisterFunction
	override fun _ready() {
		isEditing = accessoryLists.size == 2
		columnNodes = accessoryLists.mapIndexed { columnIndex, accessories ->
			val vbox = findNode("items${columnIndex + 1}") as Control
			(vbox.getParent()!!.getParent() as Control).visible = true
			slots.map { slot ->
				val panel = VBoxContainer()
				vbox.addChild(panel)
				val label = Label()
				label.text = capitalize(slot.name)
				panel.addChild(label)
				val list = VBoxContainer()
				panel.addChild(list)
				val slotAccessories = accessories.filter { it.second.slot == slot }
				populateList(list, slotAccessories)
				panel.visible = slotAccessories.any()
				list
			}
		}

		if (isEditing) {
//			columnNodes.forEachIndexed { columnIndex, column ->
//				column.forEachIndexed { listIndex, list ->
//					list.connect(
//						"item_activated", this, "on_item_activated",
//						variantArrayOf(columnIndex, listIndex)
//					)
//				}
//			}
			updateLeftListEnabled()
			updateAccessoryLists()
		}

		val list = columnNodes.firstNotNullOfOrNull { c -> c.firstOrNull { it.getChildCount() > 0 } }
		if (list != null) {
			val selection = 0
			val item = list.getChild(selection.toLong()) as AccessoryUiItem
			item.grabFocus()
			onItemSelected(item)
		}
	}

	override fun applyInput(bindings: Bindings, gamepad: Int) {
		val focused = getFocusOwner() as? AccessoryUiItem ?: return
		val (columnIndex, _) = getItemHierarchy(focused)
		if (columnIndex == -1) return

		val items = accessoryLists[columnIndex]
		val focusIndex = items.indexOfFirst { it.first == focused.key }
		val newColumnIndex = if (isEditing)
			updateMenuFocusHorizontal(bindings, gamepad, 2, columnIndex)
		else
			columnIndex

		val newIndex = updateMenuFocusVertical(bindings, gamepad, items.size, focusIndex)

		if (newColumnIndex != columnIndex || newIndex != focusIndex) {
			// For when the column is changed
			val clippedIndex = min(newIndex, accessoryLists[newColumnIndex].size - 1)
			getItemByHierarchyIndices(newColumnIndex, clippedIndex)?.grabFocus()
		}

		if (isEditing && isButtonJustReleased(bindings, gamepad, Commands.activate)) {
			val item = getItemByHierarchyIndices(newColumnIndex, newIndex)
			if (item != null) {
				onItemActivated(item)
			}
		}
	}

	override fun onClose() {
		if (isEditing) {
			val events = accessoryLists[1].mapNotNull {
				val key = it.first
				if (key is Key) {
					val accessory = newAccessory(Global.world!!.definitions, key)
					newAccessoryForContainer(actor, accessory)
				} else
					null
			}
			Global.addEvents(events)
		}
	}
}
