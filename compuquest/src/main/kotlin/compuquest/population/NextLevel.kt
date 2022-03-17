package compuquest.population

import compuquest.serving.newWorldNavigation
import compuquest.simulation.characters.Character
import compuquest.simulation.characters.copyEntity
import compuquest.simulation.general.*
import compuquest.simulation.updating.attachBodiesToScene
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
		val body = deck.bodies[actor]
		val spawner = getPlayerRespawnPoint(playerSpawners, deck, actor)
		if (body != null && spawner != null) {
			body.transform = spawner.globalTransform
		}
	}
}

fun nextLevel(world: World, materials: MaterialMap): World {
	val global = world.global
	val nextId = world.nextId.source()
	val definitions = world.definitions
	val scene = world.scene
	val level = global.level + 1
	val playerHands = extractPlayers(world.deck)
	val deck = allHandsToDeck(nextId, playerHands, persistDeck(world.deck))

	for (child in scene.getChildren().filterIsInstance<Node>()) {
		if (!deck.bodies.containsValue(child)) {
			scene.removeChild(child)
		}
	}

	val generationConfig = newGenerationConfig(definitions, deck.groups, materials, level)
	val generationHands = generateWorld(world, generationConfig)
	val playerSpawners = getPlayerSpawners(scene, deck)
	spawnExistingPlayers(playerSpawners, deck)
	val deck2 = allHandsToDeck(nextId, generationHands, deck)
	attachBodiesToScene(world.scene, deck2.bodies.values)

	return world.copy(
		deck = deck2,
		playerSpawners = playerSpawners,
		navigation = newWorldNavigation(scene),
		global = global.copy(
			level = level
		)
	)
}
