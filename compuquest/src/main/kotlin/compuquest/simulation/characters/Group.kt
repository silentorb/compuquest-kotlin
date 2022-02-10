package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import silentorb.mythic.ent.*

data class Group(
	val name: String,
	val key: String = "",
	override val relationships: Relationships = listOf(),
) : Relational

fun getGroupByKey(groups: Table<Group>, key: Key): TableEntry<Group>? =
	groups.entries.firstOrNull { it.value.key == key }

//fun getGroupSequence(deck: Deck, entity: Id): TableSequence<Group> =
//	deck.groups.asSequence()
//		.filter { group -> group.value.entities.contains(entity) }
//
//fun getGroups(deck: Deck, entity: Id): Table<Group> =
//	deck.groups
//		.filter { group -> group.value.entities.contains(entity) }

//tailrec fun getNestedGroups(
//	deck: Deck,
//	groups: Collection<TableEntry<Group>>,
//	accumulator: Table<Group> = mapOf()
//): Table<Group> =
//	if (groups.none())
//		accumulator
//	else {
//		val entity = groups.first()
//		val childGroups = getGroups(deck, entity.key)
//		val nextAccumulator = (accumulator + groups + groups.keys).distinct()
//		getNestedGroups(deck, groups.keys - nextAccumulator, nextAccumulator)
//	}
//
//fun getNestedGroups(deck: Deck, actor: Id): Table<Group> =
//	getNestedGroups(deck, listOf(actor))
