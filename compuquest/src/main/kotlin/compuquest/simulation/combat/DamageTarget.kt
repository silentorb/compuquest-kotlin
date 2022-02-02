package compuquest.simulation.combat

import compuquest.simulation.general.World
import godot.Node
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues

interface DamageTarget {
	fun onDamage(world: World, amount: Int, source: Id)
}

const val damageNodeEvent = "damageNode"

data class DamageNodeInfo(
	val node: DamageTarget,
	val amount: Int,
)

fun getDamageNodeEvents(node: Node, amount: Int): Events =
	if (node is DamageTarget)
		listOf(Event(damageNodeEvent, null, DamageNodeInfo(node, amount)))
	else
		listOf()

fun applyDamageNodeEvents(world: World, events: Events) {
	filterEventValues<DamageNodeInfo>(damageNodeEvent, events)
		.forEach { info ->
			info.node.onDamage(world, info.amount, 0L)
		}
}
