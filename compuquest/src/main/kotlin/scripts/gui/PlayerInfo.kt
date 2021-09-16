package scripts.gui

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

  fun updateResources(faction: Faction?) {
	val resources = resourcesGrid
	if (resources != null) {
	  for (child in resources.getChildren()) {
		resources.removeChild(child as Node)
		child.queueFree()
	  }

	  if (faction != null) {
		for (resource in faction.resources) {
		  val a = Label()
		  val b = Label()
		  a.text = resource.key.capitalize()
		  b.text = resource.value.toString()
		  resources.addChild(a)
		  resources.addChild(b)
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
	  lastFaction = faction
	  updateResources(faction)
	}
  }
}
