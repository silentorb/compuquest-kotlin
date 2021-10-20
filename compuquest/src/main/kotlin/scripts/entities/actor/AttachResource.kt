package scripts.entities.actor

import compuquest.simulation.general.componentGroup
import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class AttachResource : Node() {

  @Export
  @RegisterProperty
//  var resource: ResourceType = ResourceType.mana
  var resource: String = ""

  @Export
  @RegisterProperty
  var amount: Int = 0

//  @Export
//  @RegisterProperty
//  @EnumTypeHint
//  var test: Factions = Factions.neutral

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }

}
