package scripts.world

import godot.Node
import godot.Resource
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.annotation.Tool

@Tool
@RegisterClass
class AccessoryDefinition : Node() {

  @Export
  @RegisterProperty
  var range: Float = 0f

  @Export
  @RegisterProperty
  var costResource: String? = null

  @Export
  @RegisterProperty
  var costAmount: Int = 0

  @Export
  @RegisterProperty
  var cooldown: Float = 0f

  @Export
  @RegisterProperty
  var spawns: Resource? = null

}
