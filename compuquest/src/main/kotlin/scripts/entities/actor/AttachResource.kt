package scripts.entities.actor

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.componentGroup
import godot.Node
import godot.annotation.*

@RegisterClass
class AttachResource : Node() {

  @Export
  @RegisterProperty
//  var resource: ResourceType = ResourceType.mana
  var resource: String = ""

  @Export
  @RegisterProperty
  var amount: Int = 0

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }

}
