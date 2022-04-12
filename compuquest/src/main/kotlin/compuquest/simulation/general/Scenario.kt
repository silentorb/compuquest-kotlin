package compuquest.simulation.general

import compuquest.simulation.updating.simulationFps
import silentorb.mythic.ent.Key

data class Scenario(
	val name: String,
	val defaultPlayerFaction: Key,
	val playerRespawning: Boolean,
	val playerRespawnTime: Int = 5 * simulationFps,
	val characterCustomization: Boolean,
	val defaultPlayerProfession: String? = null
)
