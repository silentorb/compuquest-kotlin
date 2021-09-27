package compuquest.simulation.general

import compuquest.simulation.combat.HomingMissile
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.updating.extractComponents
import silentorb.mythic.ent.KeyTable
import silentorb.mythic.ent.Table
import silentorb.mythic.ent.genericRemoveEntities
import silentorb.mythic.ent.newDeckReflection

data class Deck(
  val accessories: Table<Accessory> = mapOf(),
  val bodies: Table<Body> = mapOf(),
  val characters: Table<Character> = mapOf(),
  val interactables: Table<Interactable> = mapOf(),
  val factions: KeyTable<Faction> = mapOf(),
  val homingMissiles: Table<HomingMissile> = mapOf(),
  val players: Table<Player> = mapOf(),
  val quests: Table<Quest> = mapOf(),
  val spirits: Table<Spirit> = mapOf(),
)

val deckReflection = newDeckReflection(Deck::class, Hand::class)

val removeEntities = genericRemoveEntities(deckReflection)

// bodies aren't added through this method because they are specially synced each frame
fun allHandsToDeck(idHands: List<Hand>, deck: Deck) =
  deck.copy(
    accessories = deck.accessories + extractComponents(idHands),
    characters = deck.characters + extractComponents(idHands),
    interactables = deck.interactables + extractComponents(idHands),
    factions = deck.factions + extractFactions(idHands),
    homingMissiles = deck.homingMissiles + extractComponents(idHands),
    players = deck.players + extractComponents(idHands),
    quests = deck.quests + extractComponents(idHands),
    spirits = deck.spirits + extractComponents(idHands),
  )
