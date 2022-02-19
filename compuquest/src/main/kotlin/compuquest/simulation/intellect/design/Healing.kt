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
import silentorb.mythic.ent.emptyId

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
	val goal = spirit.goal
	val accessory = updateSelectedHealing(world, actor)
	return if (accessory != null) {
		val visibleTarget = knowledge.visibleAllies
			.maxByOrNull { (_, character) -> character.definition.health - character.health }

		useActionOnTarget(world, actor, knowledge, accessory, visibleTarget?.key, goal)
	} else
		null
}
