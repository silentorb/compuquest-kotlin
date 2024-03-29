package compuquest.simulation.characters

import compuquest.simulation.combat.isInvisible
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInput
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import scripts.Global
import scripts.entities.CharacterBody
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.getPitchAngle
import silentorb.mythic.spatial.getYawAndPitch
import silentorb.mythic.spatial.getYawAngle

fun updatePlayerLook(body: CharacterBody, input: PlayerInput) {
	body.facing += Vector3(0, GD.deg2rad(-input.lookX), 0)
	body.head!!.rotateX(GD.deg2rad(-input.lookY))

	val tempRotation = body.head!!.rotationDegrees
	tempRotation.x = GD.clamp(tempRotation.x, -90, 90)
	body.head!!.rotationDegrees = tempRotation
}

fun updatePlayerMovement(body: CharacterBody, input: PlayerInput) {
	val moveAxis = Vector3(input.moveLengthwise.toDouble(), input.moveLateral.toDouble(), 0)
	body.moveDirection = body.directionInput(moveAxis)
}

fun playerDeathCollapse(body: Spatial, head: Spatial, killerLocation: Vector3? = null) {
	head.translation {
		y = GD.lerp(head.translation.y.toFloat(), 0.15f, 0.05f).toDouble()
	}

	if (killerLocation != null) {
		val bodyYaw = body.rotation.y.toFloat()
		val lookAt = head.globalTransform.origin.directionTo(killerLocation)
		val yaw = getYawAngle(lookAt)
		val pitch = getPitchAngle(lookAt)
		val targetYaw = (yaw - bodyYaw + Pi * 11) % (Pi * 2) - Pi
		head.rotation {
			x = GD.lerp(head.rotation.x.toFloat(), pitch, 0.1f).toDouble()
			y = GD.lerp(head.rotation.y.toFloat(), targetYaw, 0.1f).toDouble()
			z = GD.lerp(head.rotation.z.toFloat(), 0.3f, 0.05f).toDouble()
		}
	}
	else {
		head.rotation {
			z = GD.lerp(head.rotation.z.toFloat(), 0.5f, 0.05f).toDouble()
		}
	}
}

fun updatePlayerRig(world: World, actor: Id, body: CharacterBody, input: PlayerInput) {
	val deck = world.deck
	if (body.isActive) {
		updatePlayerLook(body, input)
		updatePlayerMovement(body, input)

		// Reset head transform after respawning
		if (!body.isAlive && isCharacterAlive(world.deck, actor)) {
			body.head!!.transform = body.headRestingState
			// A bug (probably with Godot Kotlin) is preventing the above line from setting rotation.
			// The below line is currently a hack that seems to get the rotation part of the transform change to stick.
			// TODO: Remove the below line of code once this engine / module bug is fixed
			body.head!!.rotation
		}

		body.isFlying = getDebugBoolean("PLAYER_FLIGHT")
		body.playerController?.environment = if (isInvisible(deck, actor))
			Global.instance!!.environments["desaturated"]!!
		else
			null

	} else {
		if (!isCharacterAlive(world.deck, actor)) {
			playerDeathCollapse(body as Spatial, body.head!!, deck.characters[actor]?.destructible?.lastDamageLocation)
		}
	}
}
