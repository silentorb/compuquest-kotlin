package compuquest.simulation.general

import compuquest.simulation.characters.*
import compuquest.simulation.combat.HomingMissile
import compuquest.simulation.combat.Missile
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.navigation.NavigationDirection
import compuquest.simulation.updating.extractComponents
import godot.Spatial
import silentorb.mythic.ent.NextId
import silentorb.mythic.ent.Table
import silentorb.mythic.ent.genericRemoveEntities
import silentorb.mythic.ent.newDeckReflection
import silentorb.mythic.timing.IntTimer

data class Deck(
	val accessories: Table<Accessory> = mapOf(),
	val bodies: Table<Spatial> = mapOf(),
	val characters: Table<Character> = mapOf(),
	val containers: Table<AccessoryContainer> = mapOf(),
	val groups: Table<Group> = mapOf(),
	val interactables: Table<Interactable> = mapOf(),
//	val factions: KeyTable<Faction> = mapOf(),
	val homingMissiles: Table<HomingMissile> = mapOf(),
	val missiles: Table<Missile> = mapOf(),
	val navigationDirections: Table<NavigationDirection> = mapOf(),
	val players: Table<Player> = mapOf(),
	val quests: Table<Quest> = mapOf(),
	val spirits: Table<Spirit> = mapOf(),
	val timers: Table<IntTimer> = mapOf(),
	val wares: Table<Ware> = mapOf(),
)

val deckReflection = newDeckReflection(Deck::class, Hand::class)

val removeEntities = genericRemoveEntities(deckReflection)

fun allIdHandsToDeck(idHands: List<Hand>, deck: Deck): Deck {

	return deck.copy(
		accessories = deck.accessories + extractComponents(idHands),
		characters = deck.characters + extractComponents(idHands),
		containers = deck.containers + extractComponents(idHands),
		bodies = deck.bodies + extractComponents(idHands),
		groups = deck.groups + extractComponents(idHands),
		interactables = deck.interactables + extractComponents(idHands),
		homingMissiles = deck.homingMissiles + extractComponents(idHands),
		missiles = deck.missiles + extractComponents(idHands),
		navigationDirections = deck.navigationDirections + extractComponents(idHands),
		players = deck.players + extractComponents(idHands),
		quests = deck.quests + extractComponents(idHands),
		spirits = deck.spirits + extractComponents(idHands),
		timers = deck.timers + extractComponents(idHands),
		wares = deck.wares + extractComponents(idHands),
	)
}

fun allHandsToDeck(nextId: NextId, hands: List<Hand>, deck: Deck): Deck =
	allIdHandsToDeck(fillHandIds(nextId, hands), deck)
