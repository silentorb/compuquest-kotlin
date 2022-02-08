package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getAccessoriesSequence
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.*
import compuquest.simulation.intellect.knowledge.getTargetRange
import compuquest.simulation.intellect.knowledge.isVisible
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import kotlin.math.abs

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character,
	visibilityRange: Float
): Table<Character> {
	val deck = world.deck
	val bodies = deck.bodies
	val body = bodies[actor] ?: return mapOf()
	val godotBody = world.bodies[actor] ?: return mapOf()
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
					&& isEnemy(world.factionRelationships, other.faction, character.faction)
					&& isVisible(world, godotBody, headLocation, id, visibilityRange)
		}
}

fun getNextTarget(
	world: World,
	visibleEnemies: Table<Character>,
	actor: Id,
	target: Id?
): Id? {
	return if (visibleEnemies.containsKey(target))
		target
	else {
		if (visibleEnemies.entries.size < 2)
			visibleEnemies.keys.firstOrNull()
		else {
			val deck = world.deck
			val bodies = deck.bodies
			val body = bodies[actor]!!
			visibleEnemies.keys
				.map { it to getTargetRange(world, body, it) }
				.minByOrNull { it.second }!!.first
		}
	}
}

fun updateDestination(world: World, actor: Id, targetLocation: Vector3?): Vector3? {
	val navigation = world.navigation
	val sourceLocation = world.deck.bodies[actor]?.translation
	if (navigation == null) {
		println("Current scene is not configured for pathfinding!")
	}
	return if (navigation != null && sourceLocation != null && targetLocation != null) {
		throw Error("Need to hook up new navigation system to updateDestination")
//		val path = navigation.getSimplePath(sourceLocation, targetLocation)
//		path.drop(1).firstOrNull { it.distanceTo(sourceLocation) > 0.1f }
	} else
		null
}

fun updateFocusedAction(world: World, actor: Id): Map.Entry<Id, Accessory>? {
	val readyActions = getReadyAccessories(world, actor)
	return readyActions.maxByOrNull { it.value.definition.range }
//world.dice.takeOneOrNull(readyActions.entries)
}

typealias AccessoryMapList = List<Map.Entry<Id, Accessory>>

fun getReadyHealingAccessories(deck: Deck, actor: Id): AccessoryMapList =
	getAccessoriesSequence(deck.accessories, actor)
		.filter { it.value.cooldown == 0f && canHeal(it.value) }
		.toList()

fun getAccessoryHealAmount(accessory: Accessory): Int =
	accessory.definition.actionEffects
		.sumOf {
			if (it.type == AccessoryEffects.heal)
				it.strengthInt
			else
				0
		}

fun getMostEfficientHealingAccessory(accessories: AccessoryMapList, gap: Int): Map.Entry<Id, Accessory> =
	accessories.map { entry ->
		val amount = getAccessoryHealAmount(entry.value)
		Pair(entry, abs(gap - amount))
	}.minByOrNull { it.second }!!
		.first

fun getMostEfficientHealingAccessory(deck: Deck, actor: Id, gap: Int): Map.Entry<Id, Accessory>? {
	val healingAccessories = getReadyHealingAccessories(deck, actor)
	return if (healingAccessories.any())
		getMostEfficientHealingAccessory(healingAccessories, gap)
	else
		null
}
