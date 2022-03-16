package compuquest.population

import compuquest.definition.monsterDistributions
import compuquest.definition.monsterLimit
import compuquest.generation.engine.GenerationConfig
import compuquest.generation.general.distributeToRaritySlots
import compuquest.simulation.characters.Relationship
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.spawnAiCharacter
import compuquest.simulation.general.Hand
import compuquest.simulation.general.Hands
import compuquest.simulation.general.PreWorld
import compuquest.simulation.intellect.newSpirit
import godot.Spatial
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.randomly.Dice
import kotlin.math.max
import kotlin.math.min

fun populateNewMonsters(world: PreWorld, config: GenerationConfig, dice: Dice, locations: Transforms): Hands {
//  println("Monster count: ${locations.size}")
	return if (locations.none())
		listOf()
	else {
		val group = config.groups.entries.firstOrNull { it.value.key == "undead" }?.key
		val relationships = if (group != null)
			listOf(
				Relationship(
					isA = RelationshipType.member,
					of = group
				)
			)
		else
			listOf()

		val distributions = distributeToRaritySlots(dice, locations.size, monsterDistributions())
		locations
			.zip(distributions) { transform, type ->
				spawnAiCharacter(world, transform, type, relationships = relationships, additional = listOf(newSpirit()))
			}
			.flatten()
	}
}

fun selectSlots(dice: Dice, slots: Slots, limit: Int): Slots {
	val count = min(limit, slots.size)
	return dice.take(slots, count)
}

fun selectSlots(attributes: Collection<String>, limit: (Int) -> Int): SlotSelector = { config, slots ->
	val filteredSlots = slots.filter { it.attributes.containsAll(attributes) }
	selectSlots(config.dice, filteredSlots, limit(config.cellCount))
}

fun populateMonsters(config: DistributionConfig, slots: Slots): Slots {
	val groundSlots = slots.filter { it.orientation == SlotOrientation.ground }
	val maxMonsters = min(monsterLimit(), groundSlots.size / max(2, 16 - config.level))
	val result = selectSlots(config.dice, groundSlots, maxMonsters)
	if (getDebugBoolean("DEBUG_MONSTER_COUNT")) {
		println("Monster Count: ${result.size}")
	}
	return result
}

fun distributeNextLevelPortals(config: DistributionConfig, slots: Slots): Slots {
	val furthest = slots
		.filter { it.orientation == SlotOrientation.ground }
		.maxByOrNull { it.transform.origin.length() }

	return listOfNotNull(furthest)
}

fun newNextLevelPortals(world: PreWorld, config: GenerationConfig, dice: Dice, transforms: Transforms): Hands {
	return transforms.map { transform ->
		val spatial = instantiateScene<Spatial>("res://entities/actor/NextLevelPortal.tscn")!!
		spatial.transform = transform
		Hand(
			components = listOf(spatial)
		)
	}
}

//fun distributeItemHands(config: GenerationConfig, nextId: NextId, dice: Dice, slots: Slots): List<Hand> {
//	val itemDefinitions = filterPropGraphs(config, setOf(DistAttributes.floor, DistAttributes.food))
//	return slots.flatMap { (_, slot) ->
//		val itemDefinition = dice.takeOne(itemDefinitions)
//		graphToHands(config.resourceInfo, nextId, itemDefinition, slot.transform)
//	}
//}
//
//fun distributeBasicProps(config: GenerationConfig, nextId: NextId, dice: Dice, slots: Slots): List<Hand> {
//	return slots.flatMap { (_, slot) ->
//		slotToHands(config, nextId, dice, slot) {
//			it
//				.minus(listOf(DistAttributes.floor, DistAttributes.wall))
//				.minus(biomeIds)
//				.none()
//		}
//	}
//}

val groundSlots = setOf(SlotOrientation.ground)

val distributors: List<Distributor> = listOf(
//    Distributor(::distributeLightSlots, ::distributeLightHands),
	Distributor(::distributeNextLevelPortals, ::newNextLevelPortals),
	Distributor(::populateMonsters, ::populateNewMonsters),
//    Distributor(selectSlots(groundSlots) { it / 10 }, ::distributeItemHands),
//    Distributor(selectSlots(groundSlots) { it * 2 / 3 }, ::distributeBasicProps),
)

fun populateDistributions(
	world: PreWorld,
	config: GenerationConfig,
	dice: Dice,
	slots: Slots,
	cellCount: Int
): List<Hand> {
	val level = getDebugInt("WORLD_LEVEL") ?: config.level
	val distributionConfig = DistributionConfig(
		cellCount = cellCount,
		level = level,
		dice = dice,
	)

	return distributors
		.fold(listOf<Hand>() to slots) { (hands, slots), distributor ->
			val selectedSlots = distributor.select(distributionConfig, slots)
			val newHands = distributor.generate(world, config, dice, selectedSlots.map { it.transform })
			Pair(hands + newHands, slots - selectedSlots)
		}
		.first
}

//fun newPlayerCharacters(nextId: NextId, definitions: Definitions): List<Hand> {
//	val playerCount = getDebugInt("INITIAL_PLAYER_COUNT") ?: 1
//	return (1..playerCount)
//		.flatMap { newPlayerAndCharacter(nextId, definitions, graph) }
//}
//
//fun addNewPlayerCharacters(nextId: NextId, config: GenerationConfig, graph: Graph, deck: Deck): Deck {
//	val hands = newPlayerCharacters(nextId, config.definitions, graph)
//	return allHandsToDeck(config.definitions, nextId, hands, deck)
//}

fun populateWorld(world: PreWorld, config: GenerationConfig, dice: Dice): List<Hand> {
//	val elementGroups = nodesToElements(config.resourceInfo, graph)
//	val lights = elementGroups.flatMap { it.lights }
	val slots = gatherSlots(world.scene)
	val hands = populateDistributions(world, config, dice, slots, config.cellCount)
	return hands
}
