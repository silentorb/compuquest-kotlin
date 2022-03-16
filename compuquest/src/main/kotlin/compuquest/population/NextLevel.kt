package compuquest.population

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.copyEntity
import compuquest.simulation.general.*
import godot.Node
import scripts.entities.PlayerSpawner

const val nextLevelEvent = "nextLevel"

fun extractPlayers(deck: Deck): Hands =
	deck.players.keys.flatMap { actor ->
		val hands = copyEntity(deck, actor)
		if (deck.players.containsKey(actor))
			hands
		else
			hands
				.map { hand ->
					hand.copy(
						components =
						hand.components.map { component ->
							if (component is Character)
								component.copy() // Not used for now but probably will be so I'm leaving this code here
							else
								component
						}
					)
				}
	}

fun persistDeck(deck: Deck): Deck =
	Deck(
		groups = deck.groups,
	)

fun spawnExistingPlayers(playerSpawners: List<PlayerSpawner>, deck: Deck) {
	for (actor in deck.players.keys) {
		val character = deck.characters[actor]
		val force = character?.relationships?.firstOrNull { it.isA == RelationshipType.member }?.of
		if (force != null) {
			val spawner = getPlayerRespawnPoint(playerSpawners, deck, force)
			val body = deck.bodies[actor]
			if (spawner != null && body != null) {
				body.translation = spawner.globalTransform.origin
			}
		}
	}
}

// nextLevel ignores runtime editor changes
fun nextLevel(world: World, materials: MaterialMap): World {
	val global = world.global
	val definitions = world.definitions
	val level = global.level + 1
	val playerHands = extractPlayers(world.deck)
	val deck = allHandsToDeck(playerHands, persistDeck(world.deck))
	for (body in deck.bodies.values) {
		body.getParent()?.removeChild(body)
	}

	for (child in world.scene.getChildren().filterIsInstance<Node>()) {
		world.scene.removeChild(child)
	}

	val generationConfig = newGenerationConfig(definitions, world.deck.groups, materials, level)
	val generationHands = generateWorld(world, generationConfig)
	val playerSpawners = getPlayerSpawners(world.scene, deck)
	spawnExistingPlayers(playerSpawners, deck)

	return world.copy(
		deck = allHandsToDeck(generationHands, deck),
		playerSpawners = playerSpawners,
		global = global.copy(
			level = level
		)
	)
}
