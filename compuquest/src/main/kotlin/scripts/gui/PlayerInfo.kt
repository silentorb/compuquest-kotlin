package scripts.gui

import compuquest.simulation.characters.Character
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.Faction
import compuquest.simulation.general.Player
import godot.Control
import godot.GridContainer
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global

@RegisterClass
class PlayerInfo : Control() {

	private var lastCharacter: Character? = null
	private var resourcesGrid: GridContainer? = null
	private val resourceIntLabels: MutableMap<Any, IntLabel> = mutableMapOf()

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

	@RegisterFunction
	override fun _ready() {
		resourcesGrid = findNode("resources") as? GridContainer
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
				lastCharacter = character
			}
		}
	}
}
