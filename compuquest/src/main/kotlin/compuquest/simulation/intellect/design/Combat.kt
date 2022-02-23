package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.*
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.knowledge.getTargetRange
import compuquest.simulation.intellect.knowledge.isEnemy
import compuquest.simulation.intellect.knowledge.isVisible
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.ent.emptyId

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character,
	visibilityRange: Float
): Table<Character> {
	val deck = world.deck
	val bodies = deck.bodies
	val body = bodies[actor] ?: return mapOf()
	val godotBody = deck.bodies[actor] as? CharacterBody ?: return mapOf()
	// Add a random offset as a heuristic to deal with complex terrain and a lack of sphere casting.
	// Occasionally, a spirit will try to attack a character through a space that is wide enough
	// for a ray but not for the spirit's projectile size.
	// This heuristic will minimize how often a spirit continuously attempts such a futile feat.
	// It also mixes up visibility checks when the target would be barely visible
	val r = 0.4f
	val randomPadding = Vector3(world.dice.getFloat(-r, r), world.dice.getFloat(-r, r), world.dice.getFloat(-r, r))
	val headLocation = body.translation + character.toolOffset + randomPadding
	return deck.characters
		.filter { (id, other) ->
			id != actor
					&& other.isAlive
					&& isEnemy(world, actor, id)
					&& isVisible(world, godotBody, headLocation, id, visibilityRange)
		}
}

fun getNextTarget(
	deck: Deck,
	characters: Table<Character>,
	actor: Id,
	target: Id?
): Id? {
	return if (characters.containsKey(target))
		target
	else {
		if (characters.entries.size < 2)
			characters.keys.firstOrNull()
		else {
			val bodies = deck.bodies
			val body = bodies[actor]!!
			characters.keys
				.map { it to getTargetRange(deck, body, it) }
				.minByOrNull { it.second }!!.first
		}
	}
}

fun getVisibleEnemy(world: World, goal: Goal, characters: Table<Character>, actor: Id): Id? {
	val lastTarget = if (goal.targetEntity != emptyId && world.dice.getInt(100) > 5)
		goal.targetEntity
	else
		null

	return getNextTarget(world.deck, characters, actor, lastTarget)
}

fun updateSelectedAttack(world: World, actor: Id): Map.Entry<Id, Accessory>? {
	val readyActions = getReadyAccessories(world.deck, actor)
		.filter { (_, accessory) ->
			accessory.definition.actionEffects.any {
				it.isAttack || it.type == AccessoryEffects.summon
			}
		}
	return readyActions.maxByOrNull { it.value.definition.range }
}

fun requiresTarget(accessory: Accessory): Boolean =
	accessory.definition.actionEffects.any { it.recipient == EffectRecipient.projectile }

fun checkCombat(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal? {
	val goal = spirit.goal
	val target = getVisibleEnemy(world, goal, knowledge.visibleEnemies, actor)
	return if (target != null) {
		val accessory = updateSelectedAttack(world, actor)
		if (accessory != null)
			useActionOnTarget(world, actor, knowledge, accessory, target, goal) ?: wait(goal)
		else
			wait(goal)
	} else
		null
}
