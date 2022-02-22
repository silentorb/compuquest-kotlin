package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.general.InteractionBehaviors
import compuquest.simulation.general.World
import compuquest.simulation.general.interactionMaxDistance
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.getFood
import compuquest.simulation.intellect.knowledge.hasFood
import compuquest.simulation.intellect.knowledge.isAParent
import compuquest.simulation.physics.getLocation
import compuquest.simulation.physics.getNearest
import scripts.entities.Bush
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id

fun checkParenting(world: World, actor: Id, character: Character, spirit: Spirit): Goal? {
	val deck = world.deck
	val goal = spirit.goal
	val bodies = deck.bodies
	return if (isAParent(spirit.knowledge)) {
		val navigation = world.navigation!!
		if (hasFood(deck, actor)) {
			val child = character.relationships
				.asSequence()
				.mapNotNull { relationship ->
					if (relationship.isA == RelationshipType.parent) {
						val child = bodies[relationship.of]
						if (child != null)
							relationship.of to child
						else
							null
					} else
						null
				}
				.firstOrNull()

			val food = getFood(deck, actor)
				.entries
				.firstOrNull()

			if (child != null && food != null) {
				val destination = child.second.globalTransform.origin
				moveWithinRange(world, actor, destination, interactionMaxDistance, goal) {
					goal.copy(
						targetEntity = child.first,
						readyTo = ReadyMode.interact,
						interactionBehavior = InteractionBehaviors.give,
						focusedAction = food.key,
					)
				}
			} else
				null
		} else {
			val previousBush = bodies[goal.targetEntity] as? Bush
			val body = bodies[actor] as CharacterBody
			val (targetEntity, bush) = if (previousBush == null || previousBush.mode != Bush.BushMode.berries) {
				val options = bodies.entries.filter { (_, value) -> value is Bush && value.mode == Bush.BushMode.berries }
				val value = getNearest(options, body.location)
				if (value != null)
					value.key to (value.value as Bush)
				else
					null to null
			} else
				goal.targetEntity to previousBush

			if (targetEntity != null && bush != null) {
				val nextGoal = goal.copy(
					targetEntity = targetEntity,
				)

				val origin = body.location
				val target = bush.globalTransform.origin
				moveWithinRange(navigation, origin, target, interactionMaxDistance, nextGoal) {
					goal.copy(
						readyTo = ReadyMode.interact,
						interactionBehavior = InteractionBehaviors.take,
					)
				}
			} else
				null
		}
	} else
		null
}
