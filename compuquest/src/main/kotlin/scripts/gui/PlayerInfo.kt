package scripts.gui

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.Faction
import compuquest.simulation.general.Player
import godot.GridContainer
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global

@RegisterClass
class PlayerInfo : Node() {

  private var lastPlayer: Player? = null
  private var lastFaction: Faction? = null
  private var resourcesGrid: GridContainer? = null
	private val resourceIntLabels: MutableMap<ResourceType, IntLabel> = mutableMapOf()

  fun updateResources(faction: Faction?, previousFaction: Faction? = null) {
	val resources = resourcesGrid
	if (resources != null) {
//	  clearChildren(resources)

	  if (faction != null) {
		for (resource in faction.resources) {
			val existing = resourceIntLabels[resource.key]
			if (existing == null) {
				val a = Label()
				val valueLabel = newIntegerLabel(resource.value)
				a.text = resource.key.name.capitalize()
				valueLabel.name = "player_resource"
				resources.addChild(a)
				resources.addChild(valueLabel)
				resourceIntLabels[resource.key] = valueLabel
			}
			else {
				existing.value = resource.value
			}
		}
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
	val player = deck?.players?.values?.firstOrNull()
	val factions = deck?.factions
	val faction = if (factions != null && player?.faction != null)
	  factions[player.faction]
	else
	  null

	if (lastPlayer != player) {
	  lastPlayer = player
	}
	if (lastFaction != faction) {
	  updateResources(faction, lastFaction)
	  lastFaction = faction
	}
  }
}
