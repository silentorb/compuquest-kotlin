package scripts.world

import godot.Node
import godot.Resource
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.annotation.Tool

@Tool
@RegisterClass
class CharacterDefinition : Node() {

  @Export
  @RegisterProperty
  var depiction: String = ""

  @Export
  @RegisterProperty
  var health: Int = 0

//  @Export
//  @RegisterProperty
//  var accessories: MutableList<Resource> = mutableListOf()

}
