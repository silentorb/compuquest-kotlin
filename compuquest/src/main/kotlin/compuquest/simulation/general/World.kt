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

interface PreWorld {
	val definitions: Definitions
	val nextId: SharedNextId
	val scene: Spatial
}

data class World(
	override val definitions: Definitions,
	val scenario: Scenario,
	override val nextId: SharedNextId,
	val zones: Map<Key, Zone> = mapOf(),
	val deck: Deck = Deck(),
	val dice: Dice,
	override val scene: Spatial,
	val step: Long = 0L, // With an update rate of 60 frames per second, this variable can safely track 48745201446 years
	val day: DayState,
	val previousEvents: Events = listOf(),
	val space: PhysicsDirectSpaceState,
	val playerSpawners: List<PlayerSpawner> = listOf(),
	val navigation: NavigationState? = null,
	val global: GlobalState = GlobalState(),
) : PreWorld {
	// This is a debug workaround not needed for production.
	// When hitting breakpoints involving a World instance, World.toString()
	// is automatically called by the IDE debugging inspector.
	// This in turn causes a chain-reaction of calls that ultimately result in Godot JVM calls that
	// sometimes throw errors.
	// The exact cause has not yet been determined.
	// This is a workaround to avoid that issue.
	override fun toString(): String = "Compuquest World"
}

fun getSpace(spatial: Spatial): PhysicsDirectSpaceState? =
	spatial.getWorld()?.directSpaceState

fun getPlayer(deck: Deck?) =
	deck?.players?.entries?.firstOrNull()

fun getPlayer(world: World?) =
	getPlayer(world?.deck)

fun getBodyEntityId(deck: Deck, body: Node): Id? =
	deck.bodies.entries.firstOrNull { it.value == body }?.key
