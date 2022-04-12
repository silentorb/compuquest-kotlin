package compuquest.definition

import compuquest.simulation.characters.CharacterSounds

object Sounds {
	const val feetLanding = "feetLanding"
	const val shootEnergy = "shootEnergy"
	const val skeletonDeath = "skeletonDeath"
	const val swishAttack = "swishAttack"
	const val vanish = "vanish"
}

object CommonCharacterSounds {
	val skeleton = CharacterSounds(
		death = Sounds.skeletonDeath,
	)
}
