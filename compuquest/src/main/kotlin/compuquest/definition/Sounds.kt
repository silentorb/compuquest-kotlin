package compuquest.definition

import compuquest.clienting.audio.SoundDefinition
import compuquest.simulation.characters.CharacterSounds

object Sounds {
	val feetLanding = "feetLanding"
	val iceWall = "iceWall"
	val playerHurt = "playerHurt"
	val shootEnergy = "shootEnergy"
	val shootFire = "shootFire"
	val skeletonDeath = "skeletonDeath"
	val summon = "summon"
	val swishAttack = "swishAttack"
	val vanish = "vanish"
}

object CommonCharacterSounds {
	val skeleton = CharacterSounds(
		death = SoundDefinition(Sounds.skeletonDeath),
	)
	val player = CharacterSounds(
		death = SoundDefinition(Sounds.skeletonDeath),
		injured = SoundDefinition(Sounds.playerHurt, 0.5f),
	)
}
