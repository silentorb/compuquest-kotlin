package scripts.gui

import compuquest.simulation.characters.Character
import compuquest.simulation.general.Deck
import compuquest.simulation.general.getOwnerAccessories
import godot.Control
import godot.GridContainer
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.clearChildren

@RegisterClass
class PlayerInfo : Control() {

	private var lastCharacter: Character? = null
	private var resourcesGrid: GridContainer? = null
	private val resourceIntLabels: MutableMap<Any, IntLabel> = mutableMapOf()
	private var buffsGrid: GridContainer? = null

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
		val buffs = getOwnerAccessories(deck.accessories, actor)
			.filter { it.value.definition.duration > 0f }

		for (buff in buffs) {
			val label = Label()
			label.text = buff.value.definition.name
			label.name = label.text
			grid.addChild(label)
		}
	}

	@RegisterFunction
	override fun _ready() {
		resourcesGrid = findNode("resources") as? GridContainer
		buffsGrid = findNode("buffs") as? GridContainer
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val world = Global.world
		val deck = world?.deck
		val player = Global.getPlayer()
		if (player == null || deck == null) {
			visible = false
		} else {
			val character = deck.characters[player.key]
			if (character == null) {
				visible = false
			} else {
				visible = true
				updateResources(character, lastCharacter)
				updateBuffs(deck, player.key)
				lastCharacter = character
			}
		}
	}
}
