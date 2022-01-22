package compuquest.population

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.*
import godot.Node
import godot.Spatial
import scripts.entities.actor.AttachResource
import silentorb.mythic.ent.NextId

//fun addPlayer(definitions: Definitions, nextId: NextId, spatial: Spatial, components: List<Node>): List<Hand> {
//  val faction = playerFaction
//  val id = nextId()
//  val memberHands = components
//    .flatMap { child ->
//      processComponentNode(definitions, id, nextId, spatial, faction, child)
//    }
//
//  return listOf(
//    Hand(
//      id = id,
//      components = listOf(
//        spatial,
//        Player(
//          faction = faction,
////          party = memberHands
////            .filter { hand -> hand.components.any { it is Character } }
////            .mapNotNull { it.id }
//        ),
//        NewFaction(faction,
//          Faction(
//            name = "Player",
//            resources = components
//              .filterIsInstance<AttachResource>()
//              .associate { attachment ->
//                (ResourceType.values().firstOrNull { it.name == attachment.resource }
//                  ?: ResourceType.none) to attachment.amount
//              }
//          )
//        )
//      )
//    )
//  ) + memberHands
//}
