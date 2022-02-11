package compuquest.simulation.characters

import silentorb.mythic.ent.Id

enum class RelationshipType {
	child,
	enemy,
	friend,
	member,
	parent,
}

data class Relationship(
	val isA: RelationshipType,
	val of: Id,
)

typealias Relationships = Collection<Relationship>

interface Relational {
	val relationships: Relationships
}
