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
	var listNodes: MutableList<ItemList> = mutableListOf()
	var maxTransfers: Int = 0
	var actor: Id = emptyId

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

			val leftList = listNodes[0]
			for (i in 0 until leftList.getItemCount()) {
				leftList.setItemDisabled(i, !enabled)
			}
		}
	}

	@RegisterFunction
	fun on_item_selected(itemIndex: Int, listIndex: Int) {
		val accessories = accessoryLists[listIndex]
		val accessory = accessories[itemIndex].value

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
	fun on_item_activated(itemIndex: Int, listIndex: Int) {
		val accessories = accessoryLists[listIndex]
		val accessoryEntry = accessories[itemIndex]
		val accessory = accessoryEntry.value
		val otherListIndex = 1 - listIndex
		accessoryLists = accessoryLists.mapIndexed { index, list ->
			if (index == listIndex)
				list - accessoryEntry
			else
				list + accessoryEntry
		}

		val listNode = listNodes[listIndex]
		val otherListNode = listNodes[otherListIndex]
		listNode.removeItem(itemIndex.toLong())
		otherListNode.addItem(resolveText(accessory.name))
		otherListNode.grabFocus()
		otherListNode.select((accessoryLists[otherListIndex].size - 1).toLong())
		updateLeftListEnabled()
	}

	fun populateList(list: ItemList, accessories: AccessoryList) {
		accessories.map { (_, accessory) ->
			list.addItem(resolveText(accessory.name))
		}
		list.visible = true
		list.connect("item_selected", this, "on_item_selected", variantArrayOf(listNodes.indexOf(list)))
	}

	@RegisterFunction
	override fun _ready() {
		listNodes.add(findNode("items") as ItemList)
		listNodes.add(findNode("items2") as ItemList)
		accessoryLists.zip(listNodes) { accessories, list ->
			populateList(list, accessories)
		}

		if (accessoryLists.size == 2) {
			listNodes.forEachIndexed { index, list ->
				list.connect("item_activated", this, "on_item_activated", variantArrayOf(index))
			}
			updateLeftListEnabled()
		}

		if (accessoryLists.any { it.any() }) {
			val selection = 0
			val list = listNodes.first()
			list.select(selection.toLong())
			list.grabFocus()
			on_item_selected(selection, 0)
		}
	}

	override fun updateFocus(bindings: Bindings, gamepad: Int) {
		val list = listNodes.firstOrNull { it.hasFocus() } ?: return

		val itemCount = list.getItemCount().toInt()
		val focusIndex = list.getSelectedItems().toList().firstOrNull() ?: 0
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
			list.select(newIndex.toLong())

			// There seems to be a bug with Godot where the above code does not fire the notification,
			// so the callback needs to be directly invoked
			on_item_selected(newIndex, listNodes.indexOf(list))
		}

		if (isButtonJustReleased(bindings, gamepad, Commands.activate)) {
			on_item_activated(newIndex, listNodes.indexOf(list))
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
