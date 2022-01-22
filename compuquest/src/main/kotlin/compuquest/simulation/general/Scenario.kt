package compuquest.simulation.general

import silentorb.mythic.ent.Key

data class Scenario(
	val name: String,
	val defaultPlayerFaction: Key,
	val playerRespawning: Boolean,
)
