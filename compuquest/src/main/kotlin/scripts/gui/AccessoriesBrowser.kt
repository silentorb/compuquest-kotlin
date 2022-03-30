package scripts.gui

import compuquest.clienting.gui.*
import compuquest.simulation.characters.AccessoryDefinition
import compuquest.simulation.characters.ProficiencyLevels
import compuquest.simulation.characters.getProficiences
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.localization.Text

typealias AccessoryList = List<Map.Entry<Any, AccessoryDefinition>>

@RegisterClass
class AccessoriesBrowser : Control() {

	var accessoryLists: List<AccessoryList> = listOf()
	var proficiencies: ProficiencyLevels = mapOf()
	var listNodes: MutableList<ItemList> = mutableListOf()
	var maxTransfers: Int = 0

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

	@RegisterFunction
	fun on_item_selected(itemIndex: Int, listIndex: Int) {
		val accessories = accessoryLists[listIndex]
		val accessory = accessories[itemIndex].value

		setLabel("name", accessory.name)
		setLabel("description", accessory.description)
		setLabel("slot", camelCaseToTitle(accessory.slot.name))
		val proficienciesList = findNode("proficiencies")!!
		clearChildren(proficienciesList)

		for (proficiency in getProficiences(accessory)) {
			val label = Button()
			label.text = camelCaseToTitle(proficiency)
			label.disabled = !proficiencies.containsKey(proficiency)
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
		}

		if (accessoryLists.any()) {
			val selection = 0
			val list = listNodes.first()
			list.select(selection.toLong())
			list.grabFocus()
			on_item_selected(selection, 0)
		}
	}
}
