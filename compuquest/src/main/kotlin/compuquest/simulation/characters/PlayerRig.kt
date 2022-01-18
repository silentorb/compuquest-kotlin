package compuquest.simulation.characters

import compuquest.simulation.general.isPlayerDead
import compuquest.simulation.input.PlayerInput
import godot.Input
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import scripts.Global
import scripts.entities.CharacterBody

fun updatePlayerLook(body: CharacterBody, input: PlayerInput) {
	body.rotateY(GD.deg2rad(-input.lookX))
	body.head!!.rotateX(GD.deg2rad(-input.lookY))

	val tempRotation = body.head!!.rotationDegrees
	tempRotation.x = GD.clamp(tempRotation.x, -90, 90)
	body.head!!.rotationDegrees = tempRotation
}

fun updatePlayerMovement(body: CharacterBody, input: PlayerInput) {
	val moveAxis = Vector3(input.moveLengthwise.toDouble(), input.moveLateral.toDouble(), 0)
	body.moveDirection = body.directionInput(moveAxis)
}

fun playerDeathCollapse(head: Spatial) {
	head.translation {
		y = GD.lerp(head.translation.y.toFloat(), 0.15f, 0.05f).toDouble()
	}

	head.rotation {
		z = GD.lerp(head.rotation.z.toFloat(), 0.5f, 0.05f).toDouble()
	}
}

fun updatePlayerRig(body: CharacterBody, input: PlayerInput) {
	if (body.isActive) {
		updatePlayerLook(body, input)
		updatePlayerMovement(body, input)
		Input.setMouseMode(Input.MOUSE_MODE_CAPTURED)
		if (isPlayerDead(Global.world?.deck)) {
			playerDeathCollapse(body.head!!)
		}
	} else {
		Input.setMouseMode(Input.MOUSE_MODE_VISIBLE)
	}
}
