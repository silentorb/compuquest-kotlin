package compuquest.simulation.physics

object CollisionMasks {
	const val none = 0
	const val dynamic = 1
	const val static = 2
	const val damageable = 4		// Can be damaged
	const val damage = 8
	const val visibility = 16		// Obstructs visibility

	const val characterLayers = dynamic or damageable
	const val characterMask = dynamic or static or damage
	const val corpseMask = static
}
