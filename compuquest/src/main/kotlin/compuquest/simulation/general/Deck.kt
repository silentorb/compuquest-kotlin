package compuquest.simulation.general

import compuquest.simulation.combat.HomingMissile
import compuquest.simulation.intellect.Spirit
import silentorb.mythic.ent.Table
import silentorb.mythic.ent.genericRemoveEntities
import silentorb.mythic.ent.newDeckReflection

data class Deck(
  val accessories: Table<Accessory> = mapOf(),
  val bodies: Table<Body> = mapOf(),
  val characters: Table<Character> = mapOf(),
  val factions: Table<Faction> = mapOf(),
  val homingMissiles: Table<HomingMissile> = mapOf(),
  val players: Table<Player> = mapOf(),
  val spirits: Table<Spirit> = mapOf(),
)

val deckReflection = newDeckReflection(Deck::class, Hand::class)

val removeEntities = genericRemoveEntities(deckReflection)
