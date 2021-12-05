package compuquest.simulation.general

import compuquest.simulation.definition.ResourceType
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

data class Ware(
  val owner: Id,
  val quantity: Int = 1,
  val price: ResourceMap,
  val quantityPerPurchase: Int = 1,
  val resourceType: ResourceType? = null,
)

const val modifyWareQuantityEvent = "modifyWareQuantity"

fun modifyWareQuantity(ware: Id, mod: Int) =
  Event(modifyWareQuantityEvent, ware, mod)

fun purchase(deck: Deck, actor: Id, ware: Id, quantity: Int): Events {
  val faction = deck.players[actor]!!.faction
  val wareRecord = deck.wares[ware]!!

  val expenseMod = wareRecord.price
    .mapValues { (_, value) -> -value * quantity }

  val resourceType = wareRecord.resourceType!!
  val gain = mapOf(resourceType to quantity + (expenseMod[resourceType] ?: 0))

  return listOf(
    modifyFactionResources(faction, expenseMod + gain),
    modifyWareQuantity(ware, -quantity)
  )
}

fun eventsFromWares(deck: Deck): Events =
  deck.wares
    .filterValues { it.quantity == 0 }
    .map { newEvent(deleteEntityCommand, it.key) }

fun updateWare(events: Events): (Id, Ware) -> Ware = { id, ware ->
  val quantityMod = events
    .filter { it.target == id && it.type == modifyWareQuantityEvent }
    .sumOf { it.value as? Int ?: 0 }

  ware.copy(
    quantity = ware.quantity + quantityMod
  )
}
