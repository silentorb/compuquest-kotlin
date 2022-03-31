package scripts.gui

import compuquest.clienting.gui.*
import compuquest.clienting.input.uiMenuNavigationBindingMap
import compuquest.simulation.characters.*
import compuquest.simulation.input.Commands
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.capitalize
import silentorb.mythic.ent.emptyId
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.RelativeButtonState
import silentorb.mythic.haft.getButtonState
import silentorb.mythic.haft.isButtonJustReleased
import silentorb.mythic.localization.Text

typealias AccessoryList = List<Map.Entry<Any, AccessoryDefinition>>

@RegisterClass
class AccessoriesBrowser : Control(), HasOnClose, HasCustomFocus {

	var accessoryLists: List<AccessoryList> = listOf()
	var proficiencies: ProficiencyLevels = mapOf()
	var columnNodes: List<List<ItemList>> = listOf()
	var maxTransfers: Int = 0
	var actor: Id = emptyId
	var selectedItem: Any? = null

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
		if (accessoryLists.size == 2) {
			val enabled = maxTransfers == 0 || accessoryLists[1].size < maxTransfers

			val column = columnNodes[0]
			for (list in column) {
				for (i in 0 until list.getItemCount()) {
					list.setItemDisabled(i, !enabled)
				}
			}
		}
	}

	fun onItemSelected(list: Control, itemIndex: Int) {
		val accessory = (list.getChild(itemIndex.toLong()) as AccessoryUiItem).accessory!!

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
	}

	@RegisterFunction
	fun on_item_selected(itemIndex: Int, columnIndex: Int, listIndex: Int) {
		val column = columnNodes[columnIndex]
		val list = column[listIndex]
		onItemSelected(list, itemIndex)
	}

	@RegisterFunction
	fun on_item_activated(itemIndex: Int, columnIndex: Int, listIndex: Int) {
		val column = columnNodes[columnIndex]
		val accessories = accessoryLists[listIndex]
		val accessoryEntry = accessories[itemIndex]
		if (accessoryEntry.key != selectedItem) {
			on_item_selected(itemIndex, columnIndex, listIndex)
			return
		}
		val accessory = accessoryEntry.value
		val otherColumn = columnNodes[1 - columnIndex]
		accessoryLists = accessoryLists.mapIndexed { index, list ->
			if (index == listIndex)
				list - accessoryEntry
			else
				list + accessoryEntry
		}

		val otherListIndex = 1 - listIndex
		val listNode = column[listIndex]
		val otherListNode = otherColumn[listIndex]
		listNode.removeItem(itemIndex.toLong())
		otherListNode.addItem(resolveText(accessory.name))
		otherListNode.grabFocus()
		otherListNode.select((accessoryLists[otherListIndex].size - 1).toLong())
		updateLeftListEnabled()
	}

	fun populateList(list: Control, accessories: AccessoryList, columnIndex: Int, index: Int) {
		accessories.map { (key, accessory) ->
//			list.addItem(resolveText(accessory.name))
			val button = AccessoryUiItem()
			button.key = key
			button.accessory = accessory
			button.text = resolveText(accessory.name)
			button.name = "button"
//			button.disabled = !(enabled == null || enabled(context, null))
			button.connect("focus_entered", this, "on_item_selected", variantArrayOf(columnIndex, index))
			button.connect("pressed", this, "on_item_activated", variantArrayOf(columnIndex, index))
			list.addChild(button)
		}
	}

	@RegisterFunction
	override fun _ready() {
		columnNodes = accessoryLists.mapIndexed { columnIndex, accessories ->
			val vbox = findNode("items${columnIndex + 1}") as ItemList
			AccessorySlot.values().map { slot ->
				val panel = VBoxContainer()
				vbox.addChild(panel)
				val label = Label()
				label.text = capitalize(slot.name)
				panel.addChild(label)
				val list = ItemList()
				panel.addChild(list)
				val slotAccessories = accessories.filter { it.value.slot == slot }
				populateList(list, slotAccessories, columnIndex, slot.ordinal)
				panel.visible = slotAccessories.any()
				list
			}
		}

		if (accessoryLists.size == 2) {
			columnNodes.forEachIndexed { columnIndex, column ->
				column.forEachIndexed { listIndex, list ->
					list.connect(
						"item_activated", this, "on_item_activated",
						variantArrayOf(columnIndex, listIndex)
					)
				}
			}
			updateLeftListEnabled()
		}

		val list = columnNodes.firstNotNullOfOrNull { c -> c.firstOrNull { it.getItemCount() > 0 } }
		if (list != null) {
			val selection = 0
			list.select(selection.toLong())
			list.grabFocus()
			onItemSelected(list, selection)
		}
	}

	override fun updateFocus(bindings: Bindings, gamepad: Int) {
		val focused = getFocusOwner() as? AccessoryUiItem ?: return
		val list = focused.getParent() as Control
//		val list = listNodes.firstNotNullOfOrNull { c -> c.firstOrNull { it.hasFocus() } } ?: return
		val columnIndex = columnNodes.indexOfFirst { it.contains(list) }
		val items = accessoryLists[columnIndex]

		val itemCount = items.size
		val focusIndex = items.indexOfFirst { it.key == focused.key }
		val newIndex = uiMenuNavigationBindingMap.keys.fold(focusIndex) { index, command ->
			val state = getButtonState(bindings, gamepad, command)
			if (state == RelativeButtonState.justReleased) {
				when (command) {
					Commands.moveUp -> wrapIndex(itemCount, index - 1)
					Commands.moveDown -> wrapIndex(itemCount, index + 1)
					else -> index
				}
			} else
				index
		}

		if (newIndex != focusIndex) {
			
////			list.select(newIndex.toLong())
//
//			// There seems to be a bug with Godot where the above code does not fire the notification,
//			// so the callback needs to be directly invoked
//			on_item_selected(newIndex, columnNodes.indexOf(list))
		}

		if (isButtonJustReleased(bindings, gamepad, Commands.activate)) {
			on_item_activated(newIndex, columnNodes.indexOf(list))
		}
	}

	override fun onClose() {
		if (accessoryLists.size == 2) {
			val events = accessoryLists[1].map {
				val accessory = newAccessory(Global.world!!.definitions, it.value.key)
				newAccessoryForContainer(actor, accessory)
			}
			Global.addEvents(events)
		}
	}
}
