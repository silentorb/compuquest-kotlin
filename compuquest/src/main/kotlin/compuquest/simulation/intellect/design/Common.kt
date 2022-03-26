package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Accessory
import compuquest.simulation.general.World
import compuquest.simulation.intellect.knowledge.Knowledge
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.TableEntry

fun useAction(accessory: Id, goal: Goal): Goal =
	goal.copy(
		focusedAction = accessory,
		readyTo = ReadyMode.action,
	)

fun useActionOnTarget(world: World, actor: Id, accessory: TableEntry<Accessory>, target: Id, goal: Goal): Goal? =
	if (requiresTarget(accessory.value)) {
		val range = accessory.value.definition.useRange
		moveWithinRange(world, actor, target, range, goal) {
			goal.copy(
				focusedAction = accessory.key,
				targetEntity = target,
				readyTo = ReadyMode.action
			)
		}
	} else
		goal.copy(
			focusedAction = accessory.key,
			targetEntity = target,
			readyTo = ReadyMode.action,
		)

fun followingNoLongerSeenTarget(knowledge: Knowledge, goal: Goal): Goal? {
	val destination = knowledge.entityLocations[goal.targetEntity]
	return if (destination != null)
		goal.copy(
			destination = destination,
			readyTo = ReadyMode.move,
		)
	else
		null
}

fun useActionOnTarget(
	world: World,
	actor: Id,
	knowledge: Knowledge,
	accessory: TableEntry<Accessory>,
	target: Id?,
	goal: Goal
): Goal? =
	if (target != null)
		useActionOnTarget(world, actor, accessory, target, goal)
	else
		followingNoLongerSeenTarget(knowledge, goal)
