package scripts.entities

import compuquest.simulation.general.getBodyEntityId
import compuquest.simulation.general.getOwnerAccessories
import godot.Camera
import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import scripts.Global
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id

@RegisterClass
class CharacterFloatingTextController : Node() {

	@Export
	@RegisterProperty
	var labelPath: NodePath? = null

	var label: Node? = null
	var actor: Id? = null

	fun setText(value: String) {
		label?.set("text", value)
	}

	@RegisterFunction
	override fun _ready() {
		label = getNode(labelPath!!)
		setText("")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val world = Global.world
		if (world != null) {
			if (actor == null) {
				actor = getBodyEntityId(world, getParent() as Spatial)
			}
			val localActor = actor
			if (localActor != null) {
				val text = if (getDebugBoolean("DISPLAY_CHARACTER_IDS"))
					actor.toString()
				else
					getOwnerAccessories(world, localActor)
						.filter { it.value.definition.passiveEffects.any() }
						.map { it.value.definition.name }
						.joinToString("\n")

				setText(text)
			}
		}
	}
}
