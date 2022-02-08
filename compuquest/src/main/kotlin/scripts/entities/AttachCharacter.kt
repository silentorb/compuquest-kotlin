package scripts.entities

import compuquest.definition.characterDefinitions
import compuquest.definition.staticCharacterDefinitions
import godot.AnimatedSprite3D
import godot.Engine
import godot.Node
import godot.annotation.*
import scripts.Global

@Tool
@RegisterClass
class AttachCharacter : Node() {

	@Export
	@RegisterProperty
	var type: String = ""
		set(value) {
			if (Engine.editorHint) {
				if (field != value) {
					val parent = getParent()
					if (parent != null) {
						val sprite = parent.findNode("sprite") as? AnimatedSprite3D
						if (sprite != null) {
							val character = staticCharacterDefinitions[value]
							if (character == null) {
								sprite.animation = ""
								sprite.frame = 0
							} else {
								sprite.animation = character.depiction
								sprite.frame = character.frame.toLong()
							}
						}
					}
				}
			}
			field = value
		}

	@Export
	@RegisterProperty
	var faction: String = ""

	@RegisterFunction
	override fun _process(delta: Double) {
		// If the world is not null then the data from this node must have already been extracted and integrated,
		// at which point this node is no longer needed.
		if (!Engine.editorHint && Global.world != null) {
			queueFree()
		}
	}
}
