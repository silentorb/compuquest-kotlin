package scripts.gui

import compuquest.simulation.characters.Character
import compuquest.simulation.general.Deck
import compuquest.simulation.characters.getOwnerAccessories
import godot.Control
import godot.GridContainer
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.findParentOfType

@RegisterClass
class PlayerInfo : Control() {

	var lastCharacter: Character? = null
	var resourcesGrid: GridContainer? = null
	val resourceIntLabels: MutableMap<Any, IntLabel> = mutableMapOf()
	var buffsGrid: GridContainer? = null
	var nameLabel: Label? = null
	var actor: Id = 0L

	fun updateResource(resources: GridContainer, key: String, value: Int) {
		val existing = resourceIntLabels[key]
		if (existing == null) {
			val a = Label()
			val valueLabel = newIntegerLabel(value)
			a.text = key.capitalize()
			valueLabel.name = "player_resource"
			resources.addChild(a)
			resources.addChild(valueLabel)
			resourceIntLabels[key] = valueLabel
		} else {
			existing.value = value
		}
	}

	fun updateResources(character: Character, previousCharacter: Character? = null) {
		val resources = resourcesGrid
		if (resources != null) {
			if (character.health != previousCharacter?.health) {
				updateResource(resources, "health", character.health)
			}
		}
	}

	fun updateBuffs(deck: Deck, actor: Id) {
		val grid = buffsGrid!!
		clearChildren(grid)
//		val buffs = getOwnerAccessories(deck, actor)
//			.filter { it.value.definition.duration > 0f }
//
//		for (buff in buffs) {
//			val label = Label()
//			label.text = buff.value.definition.key
//			label.name = label.text
//			grid.addChild(label)
//		}
	}

	@RegisterFunction
	override fun _ready() {
		resourcesGrid = findNode("resources") as? GridContainer
		buffsGrid = findNode("buffs") as? GridContainer
		nameLabel = findNode("name") as? Label
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val world = Global.world
		val deck = world?.deck
		if (actor == 0L) {
			actor = findParentOfType<Hud>(this)?.actor ?: 0L
		}
		if (actor == 0L || deck == null) {
			visible = false
		} else {
			val character = deck.characters[actor]
			if (character == null) {
				visible = false
			} else {
				visible = true
				updateResources(character, lastCharacter)
				updateBuffs(deck, actor)
				nameLabel?.text = character.name
				lastCharacter = character
			}
		}
	}
}
