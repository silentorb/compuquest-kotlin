package scripts.gui

import compuquest.simulation.general.getPlayer
import godot.GridContainer
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class MemberManagement : Node() {

  private var _grid: GridContainer? = null
  private var lastMembers: Set<Id> = setOf()

  @RegisterFunction
  override fun _ready() {
    _grid = findNode("grid") as? GridContainer
  }

  fun updateGrid(members: Set<Id>) {
    val grid = _grid!!
    clearChildren(grid)
    for (member in members) {
      val portrait = instantiateScene<Portrait>("res://gui/hud/Portrait.tscn")!!
      portrait.member = member
      grid.addChild(portrait)
    }
  }

  @RegisterFunction
  override fun _process(delta: Double) {
    tempCatch {
      val world = Global.world
      val deck = world?.deck
      val player = getPlayer(world)?.value
      if (deck != null && player != null) {
//		val members = getNonPartyMembers(deck, player)
//		if (lastMembers != members) {
//		  lastMembers = members
//		  updateGrid(members)
//		}
      }
    }
  }
}
