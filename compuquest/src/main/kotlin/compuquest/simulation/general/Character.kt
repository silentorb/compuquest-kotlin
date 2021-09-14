package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

data class Character(
  val name: String,
  val faction: Id,
  val depiction: Key,
  val health: IntResource,
  val body: Id? = null,
) {
  val isAlive: Boolean = health.value > 0
}

fun getAccessories(accessories: Table<Accessory>, actor: Id) =
  accessories.filterValues { it.owner == actor }

fun canUse(world: World, accessory: Accessory): Boolean {
  val deck = world.deck
  val actor = deck.characters[accessory.owner]
  val faction = deck.factions[actor?.faction]
  val cost = accessory.cost
  return accessory.cooldown == 0f && (faction == null ||
      (cost == null || faction.resources[cost.resource] ?: 0 >= cost.amount)
      )
}
