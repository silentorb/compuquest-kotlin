package scripts.entities

import godot.PackedScene
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class PlayerSpawner : Spatial() {
	@Export
	@RegisterProperty
	var scene: PackedScene? = null

	@Export
	@RegisterProperty
	var type: String = "player" // Should be Characters.player but references like that aren't supported by Godot Kotlin yet

	@Export
	@RegisterProperty
	var faction: String = ""
}
