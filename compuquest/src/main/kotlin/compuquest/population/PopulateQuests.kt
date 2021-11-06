package compuquest.population

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.Quest
import compuquest.simulation.general.QuestTypes
import compuquest.simulation.general.World

fun populateQuests(world: World): World {
  val deck = world.deck
  val nextId = world.nextId.source()
  val dice = world.dice
  val clients = deck.characters.filter { it.value.attributes.contains("quests") }
  val quests = clients
    .flatMap { (client, _) ->
      val recipients = dice.take(clients.minus(client).entries, dice.getInt(0, 2))
      recipients.map { recipient ->
        Quest(
          name = "Quest ${recipient.value.name}",
          client = client,
          type = QuestTypes.delivery,
          reward = mapOf(ResourceType.mana to 200),
          recipient = recipient.key,
        )
      }
    }

  return world.copy(
    deck = deck.copy(
      quests = deck.quests + quests.associateBy { nextId() },
    )
  )
}
