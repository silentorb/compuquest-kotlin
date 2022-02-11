package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Zone
import compuquest.simulation.intellect.navigation.NavigationState
import godot.*
import scripts.entities.PlayerSpawner
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events
import silentorb.mythic.randomly.Dice

data class World(
	val definitions: Definitions,
	val scenario: Scenario,
	val nextId: SharedNextId,
	val bodies: Table<Spatial> = mapOf(),
	val sprites: Table<AnimatedSprite3D> = mapOf(),
	val zones: Map<Key, Zone> = mapOf(),
	val deck: Deck = Deck(),
	val dice: Dice,
	val scene: Spatial,
	val step: Long = 0L, // With an update rate of 60 frames per second, this variable can safely track 48745201446 years
	val day: DayState,
	val previousEvents: Events = listOf(),
	val space: PhysicsDirectSpaceState,
	val playerSpawners: List<PlayerSpawner> = listOf(),
	val navigation: NavigationState?,
)

fun getSpace(spatial: Spatial): PhysicsDirectSpaceState? =
	spatial.getWorld()?.directSpaceState

fun getPlayer(deck: Deck?) =
	deck?.players?.entries?.firstOrNull()

fun getPlayer(world: World?) =
	getPlayer(world?.deck)

fun getBodyEntityId(world: World, body: Node): Id? =
	world.bodies.entries.firstOrNull { it.value == body }?.key
