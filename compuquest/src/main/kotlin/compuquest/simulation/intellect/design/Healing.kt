package compuquest.simulation.intellect.design

import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.Accessory
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.EffectRecipient
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.knowledge.getTargetRange
import silentorb.mythic.ent.Id

fun updateSelectedHealing(world: World, actor: Id): Map.Entry<Id, Accessory>? {
	val readyActions = getReadyAccessories(world, actor)
		.filter { (_, accessory) ->
			accessory.definition.actionEffects.any {
				it.type == AccessoryEffects.heal && it.recipient == EffectRecipient.raycast
			}
		}
	return readyActions.maxByOrNull { it.value.definition.range }
}

fun checkHealing(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal? {
	val deck = world.deck
	val goal = spirit.goal
	val accessory = updateSelectedHealing(world, actor)

	return if (accessory != null) {
		val visibleTarget = knowledge.visibleAllies
			.maxByOrNull { (_, character) -> character.definition.health - character.health }

		val body = deck.bodies[actor]
		val targetRange = if (body != null && visibleTarget != null)
			getTargetRange(deck, body, visibleTarget.key)
		else
			null

		val isInRange =
			(!requiresTarget(accessory.value) || (targetRange != null && targetRange <= accessory.value.definition.range))

		val lastKnownTargetLocation = knowledge.entityLocations[goal.targetEntity]

		val destination = when {
			visibleTarget != null && targetRange != null && !isInRange ->
				world.deck.bodies[visibleTarget.key]?.translation
			visibleTarget == null && lastKnownTargetLocation != null -> lastKnownTargetLocation
			isInRange -> null
			else -> null
		}

		val nextTarget = visibleTarget?.key
			?: if (goal.targetEntity != null && deck.characters[goal.targetEntity]?.isAlive != true)
				null
			else
				goal.targetEntity

		if (nextTarget != null || destination != null)
			goal.copy(
				focusedAction = accessory.key,
				targetEntity = nextTarget,
				destination = destination,
				readyTo = when {
					destination != null -> ReadyMode.move
					isInRange -> ReadyMode.action
					else -> ReadyMode.none
				},
			)
		else
			null
	} else
		null
}
