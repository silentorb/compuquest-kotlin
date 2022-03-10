package compuquest.population

import compuquest.simulation.characters.*
import compuquest.simulation.general.*
import compuquest.simulation.intellect.newSpirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Spatial
import godot.core.NodePath
import scripts.entities.AttachCharacter
import scripts.entities.AttachRelationship
import scripts.entities.PlayerSpawner
import scripts.world.GroupNode
import scripts.world.RelationshipNode
import scripts.world.WorldGenerator
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.godoting.findChildrenOfType
import silentorb.mythic.godoting.getChildrenOfType

fun mapNodeToId(deck: Deck, node: Node?): Id? =
	if (node != null)
		getBodyEntityId(deck, node) ?: if (node is GroupNode && node.key != "")
			deck.groups.entries.firstOrNull { it.value.key == node.key }?.key
		else
			null
	else
		null

fun mapNodeToId(deck: Deck, anchor: Node, path: NodePath?): Id? =
	if (path == null)
		null
	else {
		val node = anchor.getNode(path)
		mapNodeToId(deck, node)
	}

fun <T : Relational> applyRelationships(
	relationships: Table<Relationships>,
	table: Table<T>,
	copy: (Relationships, T) -> T
): Table<T> =
	table.mapValues { (id, record) ->
		val additionalRelationships = relationships[id] ?: listOf()
		copy(record.relationships + additionalRelationships, record)
	}

fun getRelationshipType(name: String): RelationshipType? =
	RelationshipType.values().firstOrNull { it.toString() == name }

fun getDirectRelationshipAttachments(deck: Deck, root: Node): Relationships =
	getChildrenOfType<AttachRelationship>(root)
		.mapNotNull { node ->
			val isA = getRelationshipType(node.isA)
			val of = mapNodeToId(deck, node, node.of)
			if (of != null && isA != null) {
				Relationship(isA, of)
			} else
				null
		}

fun applyRelationships(world: World): World {
	val attachedRelationships = findChildrenOfType<AttachRelationship>(world.scene)
		.mapNotNull { node ->
			val entity = mapNodeToId(world.deck, node.getParent())
			val isA = getRelationshipType(node.isA)
			val of = mapNodeToId(world.deck, node, node.of)
			if (entity != null && of != null && isA != null) {
				Triple(entity, isA, of)
			} else
				null
		}

	val dualRelationships = findChildrenOfType<RelationshipNode>(world.scene)
		.flatMap { node ->
			val entity = mapNodeToId(world.deck, node, node.entity)
			val of = mapNodeToId(world.deck, node, node.of)
			val isA = getRelationshipType(node.isA)
			if (entity != null && of != null && isA != null) {
				listOf(
					Triple(entity, isA, of),
					Triple(of, isA, entity),
				)
			} else
				listOf()
		}

	val relationships: Table<Relationships> = (dualRelationships + attachedRelationships)
		.groupBy { it.first }
		.mapValues { (_, value) -> value.map { Relationship(it.second, it.third) } }

	return world
		.copy(
			deck = world.deck.copy(
				characters = applyRelationships(relationships, world.deck.characters) { r, c -> c.copy(relationships = r) },
				groups = applyRelationships(relationships, world.deck.groups) { r, c -> c.copy(relationships = r) },
			)
		)
}

fun processSceneEntities(scene: Node, world: World): World {
	val definitions = world.definitions
	val nextId = world.nextId.source()
	val worldGenerators = findChildrenOfType<WorldGenerator>(scene)
	val (blockGrid, generationBundle) = generateWorld(world, worldGenerators)
	for (spatial in generationBundle.spatials) {
		scene.addChild(spatial)
	}
	val attachments = findChildrenOfType<AttachCharacter>(scene)
	val groups = findChildrenOfType<GroupNode>(scene)
		.associate {
			nextId() to Group(
				name = it.name,
				key = it.key,
			)
		}

	val world2 = world.copy(
		deck = world.deck.copy(
			groups = world.deck.groups + groups,
		),
	)

	val entityNodeHands = findChildrenOfType<EntityNode>(scene)
		.filterIsInstance<Spatial>()
		.map { Hand(components = listOf(it)) }

	val characterHands = if (getDebugBoolean("NO_MONSTERS"))
		listOf()
	else
		attachments
			.flatMap { attachment ->
				addCharacter(
					definitions,
					nextId(),
					nextId,
					attachment,
					listOf(newSpirit())
				)
			}

	val hands = characterHands + entityNodeHands + generationBundle.hands

	val playerSpawners = findChildrenOfType<PlayerSpawner>(scene)
	for (playerSpawner in playerSpawners) {
		playerSpawner.relationships = getDirectRelationshipAttachments(world2.deck, playerSpawner)
	}

	val world3 = newEntitiesFromHands(hands, world2)
		.copy(
			playerSpawners = playerSpawners,
		)

	return applyRelationships(world3)
}
