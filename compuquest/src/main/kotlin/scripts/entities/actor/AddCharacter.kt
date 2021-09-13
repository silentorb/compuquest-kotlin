package scripts.entities.actor

import compuquest.simulation.general.*
import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global

@RegisterClass
class AddCharacter : Node() {

  @Export
  var characterName: String = ""

  @Export
  var depiction: String = ""

  @Export
  var faction: String = ""

  @Export
  var healthValue: Int = 0

  @Export
  var healthMax: Int = 0

  @RegisterFunction
  override fun _ready() {
    val newCharacter = NewCharacter(
      character = Character(
        name = characterName,
        faction = faction,
        depiction = depiction,
        health = IntResource(healthValue, healthMax),
      ),
      body = getParent() as Spatial,
    )

    Global.addCommand(
      Command(addCharacterCommand, value = newCharacter)
    )
  }
}
