package scripts.entities

import compuquest.simulation.characters.Relationships
import godot.PackedScene
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.core.NodePath
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId

@RegisterClass
class PlayerSpawner : Spatial() {
	@Export
	@RegisterProperty
	var scene: PackedScene? = null

	@Export
	@RegisterProperty
	var type: String = "player" // Should be Characters.player but references like that aren't supported by Godot Kotlin yet

	var relationships: Relationships = listOf()
}
