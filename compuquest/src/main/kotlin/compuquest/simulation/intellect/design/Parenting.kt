package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.general.World
import compuquest.simulation.general.interactionMaxDistance
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.hasFood
import compuquest.simulation.intellect.knowledge.isAParent
import compuquest.simulation.physics.getNearest
import scripts.entities.Bush
import silentorb.mythic.ent.Id

fun checkParenting(world: World, actor: Id, character: Character, spirit: Spirit): Goal? {
	val deck = world.deck
	val goal = spirit.goal
	return if (isAParent(spirit.knowledge)) {
		if (hasFood(deck, actor)) {
			val child = character.relationships
				.asSequence()
				.mapNotNull { relationship ->
					if (relationship.isA == RelationshipType.parent) {
						val child = world.bodies[relationship.of]
						if (child != null)
							relationship.of to child
						else
							null
					} else
						null
				}
				.firstOrNull()

			if (child != null) {
				val body = world.bodies[actor]!!
				val destination = child.second.globalTransform.origin
				moveWithinRange(body.globalTransform.origin, destination, interactionMaxDistance, goal) {
					goal.copy(
						targetEntity = child.first,
						readyTo = ReadyMode.interact,
					)
				}
			} else
				null
		} else {
			val previousBush = world.bodies[goal.targetEntity] as? Bush
			val body = world.bodies[actor]!!
			val (targetEntity, bush) = if (previousBush == null || previousBush.mode != Bush.BushMode.berries) {
				val options = world.bodies.entries.filter { (_, value) -> value is Bush && value.mode == Bush.BushMode.berries }
				val value = getNearest(options, body.globalTransform.origin)
				if (value != null)
					value.key to (value.value as Bush)
				else
					null to null
			} else
				goal.targetEntity to previousBush

			if (targetEntity != null && bush != null)
				moveWithinRange(body.globalTransform.origin, bush.globalTransform.origin, interactionMaxDistance, goal) {
					goal.copy(
						readyTo = ReadyMode.interact,
					)
				} else
				null
		}
	} else
		null
}
