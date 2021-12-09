package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Zone
import godot.Navigation
import godot.Node
import godot.PhysicsDirectSpaceState
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.happening.Events
import silentorb.mythic.randomly.Dice

data class World(
	val definitions: Definitions,
	val nextId: SharedNextId,
	val bodies: Map<Id, Spatial> = mapOf(),
	val sprites: Map<Id, Spatial> = mapOf(),
	val zones: Map<Key, Zone> = mapOf(),
	val deck: Deck = Deck(),
	val factionRelationships: RelationshipTable = mapOf(),
	val dice: Dice,
	val scene: Node? = null,
	val step: Long = 0L, // With an update rate of 60 frames per second, this variable can safely track 48745201446 years
	val day: DayState,
	val previousEvents: Events = listOf(),
	val navigation: Navigation? = null
)

fun getSpace(world: World): PhysicsDirectSpaceState? =
	world.bodies.values.firstOrNull()?.getWorld()?.directSpaceState

fun getPlayer(deck: Deck?) =
	deck?.players?.entries?.firstOrNull()

fun getPlayer(world: World?) =
	getPlayer(world?.deck)
