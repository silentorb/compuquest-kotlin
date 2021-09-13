package compuquest.simulation.general

import godot.Spatial

data class Character(
  val name: String,
  val faction: Key,
  val depiction: Key,
  val health: IntResource,
  val body: Id? = null,
)

data class NewCharacter(
  val character: Character,
  val body: Spatial,
)

const val addCharacterCommand = "addCharacter"
