package scripts.world

import godot.MeshInstance
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class PropMesh : MeshInstance() {
	enum class Attribute {
		notUsed,
		ceiling,
		floor,
		wall,
	}

	@Export
	@RegisterProperty
	var attributes = setOf(Attribute.wall)
}
