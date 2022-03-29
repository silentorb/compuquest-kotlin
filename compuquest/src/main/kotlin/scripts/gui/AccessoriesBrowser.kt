package scripts.gui

import compuquest.clienting.gui.*
import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.AccessoryDefinition
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.localization.Text

@RegisterClass
class AccessoriesBrowser : Control() {

	var accessories: List<Map.Entry<Id, AccessoryDefinition>> = listOf()
	var itemsContainer: ItemList? = null

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
	fun on_item_selected(index: Int) {
		val accessory = accessories[index].value

		setLabel("name", accessory.name)
		setLabel("description", accessory.description)
		setLabel("slot", camelCaseToTitle(accessory.slot.name))
		val proficienciesList = findNode("proficiencies")!!
		clearChildren(proficienciesList)
		val proficiencies = (accessory.actionEffects.flatMap { it.proficiencies } +
				accessory.passiveEffects.flatMap { it.proficiencies })
			.distinct()

		for (proficiency in proficiencies) {
			val label = Label()
			label.text = camelCaseToTitle(proficiency)
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
	override fun _ready() {
		itemsContainer = findNode("items") as? ItemList
		val list = itemsContainer!!
		accessories.map { (_, accessory) ->
			list.addItem(resolveText(accessory.name))
		}
		list.connect("item_selected", this, "on_item_selected")

		val selection = 0
		list.select(selection.toLong())
		list.grabFocus()
		on_item_selected(selection)
	}
}
