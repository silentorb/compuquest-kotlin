package compuquest.clienting.multiplayer

import silentorb.mythic.ent.Id
import silentorb.mythic.haft.PlayerMap

fun updateClientPlayers(worldPlayers: Collection<Id>, clientPlayers: PlayerMap): PlayerMap =
	if (worldPlayers.size == clientPlayers.size && clientPlayers.all { worldPlayers.contains(it.key) })
		clientPlayers
	else {
		val same =
			clientPlayers
				.filterKeys { worldPlayers.contains(it) }

		val availableSlots = (0..3).minus(same.values).map { it }
		val additions = worldPlayers
			.minus(clientPlayers.keys)
			.zip(availableSlots) { a, b -> a to b }
			.associate { it }

		same + additions
	}
