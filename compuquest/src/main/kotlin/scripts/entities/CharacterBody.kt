package scripts.entities

import compuquest.simulation.characters.Character
import compuquest.simulation.input.PlayerInput
import godot.Camera
import godot.Spatial
import godot.core.Transform
import godot.core.Vector3
import silentorb.mythic.ent.Id

interface CharacterBody : AnimatedSprite3DOwner {
	var head: Spatial?
	var velocity: Vector3
	var isSlowed: Boolean
	var isAlive: Boolean
	var headRestingState: Transform
	var actor: Id
	var isActive: Boolean
	var moveDirection: Vector3
	var radius: Float
	var globalTransform: Transform
	var translation: Vector3
	var facing: Vector3
	var walkSpeed: Float
	var speed: Float
	var location: Vector3
	var isFlying: Boolean
	var playerController: PlayerController?

	fun getToolTransform(): Transform
	fun update(input: PlayerInput, character: Character, delta: Float)
	fun directionInput(moveAxis: Vector3): Vector3
}
