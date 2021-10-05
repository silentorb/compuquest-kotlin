package compuquest.population

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.Hand
import compuquest.simulation.general.Ware
import godot.Resource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.getInt
import silentorb.mythic.godoting.getResourceType

fun addWare(nextId: NextId, owner: Id, node: Resource): Hand {
  return Hand(
    id = nextId(),
    components = listOf(
      Ware(
        owner = owner,
        quantity = getInt(node, "quantity"),
        price = mapOf((getResourceType(node, "priceType") ?: ResourceType.gold) to getInt(node, "pricePerUnit")),
        quantityPerPurchase = getInt(node, "quantityPerPurchase"),
        resourceType = getResourceType(node, "resourceType") ?: ResourceType.mana,
      )
    )
  )
}
