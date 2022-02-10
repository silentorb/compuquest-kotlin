package compuquest.simulation.general

import compuquest.simulation.definition.TypedResource
import compuquest.simulation.definition.ResourceType
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.KeyTable
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.randomly.Dice

data class Faction(
	val name: String,
	val resources: ResourceMap = mapOf(),
	val paysFees: Boolean = true,
	val nextInvoiceNumber: Long = 1L,
	val newUnpaidInvoices: List<Invoice> = listOf(),
	val oldUnpaidInvoices: List<Invoice> = listOf(),
) {
	fun getResource(type: ResourceType): Int =
		resources[type] ?: 0

	val gold: Int = getResource(ResourceType.gold)
}

const val removeFactionMemberEvent = "removeFactionMember"
const val modifyFactionResourcesEvent = "modifyFactionResource"

//enum class RelationshipCategory {
//	war,
//	neutral,
//	ally
//}

fun modifyFactionResources(faction: Key, mod: ResourceMap) =
	Event(modifyFactionResourcesEvent, faction, mod)

//fun getRelationshipValue(relationships: Relationships, first: Key?, second: Key?): Int =
//	relationships[setOf(first, second)] ?: 50

//val relationshipDivisions = listOf(
//	35, 65
//)

//typealias RelationshipTable = Map<Set<Key>, Int>

//fun getRelationshipCategory(value: Int): RelationshipType {
//	val index = relationshipDivisions.indexOfFirst { it <= value } + 1
//	return RelationshipCategory.values()[index]
//}

//fun hasRelationship(relationships: Relationships, first: Id, second: Id, type: RelationshipType): Boolean =
//	relationships.any {
//		((it.entity == first && it.of == second) || (it.entity == second && it.of == first)) &&
//				it.isA == type
//	}
//
//fun hasRelationship(world: World, first: Id?, second: Id?, type: RelationshipType): Boolean =
//	if (first == null || second == null)
//		false
//	else {
//		val relationships = world.relationships
//		hasRelationship(relationships, first, second, type) ||
//				getGroupSequence(world.deck, second)
//					.any { hasRelationship(relationships, first, it.key, type) }
//	}
//
//fun isEnemy(world: World, first: Id?, second: Id?): Boolean =
//	hasRelationship(world, first, second, RelationshipType.enemy)
//
//fun isFriendly(world: World, first: Id?, second: Id?): Boolean =
//	!isEnemy(world, first, second)

data class NewFaction(
	val key: String,
	val data: Faction,
)

fun extractFactions(idHands: List<Hand>): KeyTable<Faction> =
	idHands
		.flatMap { hand ->
			hand.components.filterIsInstance<NewFaction>()
				.map { it.key to it.data }
		}
		.associate { it }

data class Invoice(
	val number: Long,
	val employee: Id,
	val amountDue: Int,
)

typealias Invoices = List<Invoice>

//fun paydayInvoices(world: World, key: Key, nextInvoiceNumber: Long): Invoices {
//	val deck = world.deck
//	return deck.characters
//		.filterValues { it.faction == key && it.fee > 0 }
//		.entries.mapIndexed { index, entry ->
//			Invoice(
//				number = nextInvoiceNumber + index,
//				employee = entry.key,
//				amountDue = entry.value.fee
//			)
//		}
//}

tailrec fun payInvoices(dice: Dice, gold: Int, invoices: Invoices, accumulator: Invoices): Invoices =
	if (invoices.none())
		accumulator
	else {
		val next = dice.takeOneOrNull(invoices.filter { it.amountDue <= gold })
		if (next == null)
			accumulator
		else
			payInvoices(dice, gold - next.amountDue, invoices.drop(1), accumulator + next)
	}

fun payInvoices(dice: Dice, gold: Int, invoices: Invoices): Invoices =
	if (invoices.sumOf { it.amountDue } < gold)
		invoices
	else
		payInvoices(dice, gold, invoices, listOf())

fun updateFactionResources(
	adjustments: List<TypedResource>,
	resources: ResourceMap,
): ResourceMap {
	return resources.mapValues { (resourceType, amount) ->
		val adjustment = adjustments
			.filter { cost -> cost.first == resourceType }
			.sumOf { cost -> cost.second }

		amount + adjustment
	}
}

//fun updateFaction(world: World, events: Events): (Key, Faction) -> Faction {
//	val dice = world.dice
////  val uses = getUseEvents(world.deck, events)
//	val isPayday = events.any { it.type == newDayEvent }
//
//	return { key, faction ->
//		val invoices = if (isPayday)
//			paydayInvoices(world, key, faction.nextInvoiceNumber)
//		else
//			listOf()
//
//		val gold = faction.gold
//		val paidInvoices = payInvoices(dice, gold, invoices)
//		val unpaidInvoices = invoices - paidInvoices
//
//		val basicAdjustments: List<TypedResource> = events.filter { event ->
//			event.type == modifyFactionResourcesEvent && event.target == key
//		}
//			.flatMap { event -> (event.value as ResourceMap).map { it.key to it.value } }
//
////    val adjustments = uses
////      .filter { f -> f.first == key }
////      .map { it.second } +
////        basicAdjustments +
////        if (paidInvoices.any())
////          listOf(TypedResource(ResourceType.gold, -paidInvoices.sumOf { it.amountDue }))
////        else
////          listOf()
//
//		faction.copy(
////      resources = updateFactionResources(adjustments, faction.resources),
//			nextInvoiceNumber = faction.nextInvoiceNumber + invoices.size,
//			newUnpaidInvoices = unpaidInvoices,
//			oldUnpaidInvoices = faction.oldUnpaidInvoices + faction.newUnpaidInvoices
//		)
//	}
//}

fun eventsFromFaction(): (Key, Faction) -> Events = { _, faction ->
	faction.newUnpaidInvoices
		.groupBy { it.employee }
		.map { Event(removeFactionMemberEvent, it.key, it) }
}
