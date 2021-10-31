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
      val recipients = dice.take(clients.minus(client).entries, dice.getInt(1, 3))
      recipients.map { recipient ->
        Quest(
          name = "Quest ${recipient.value.name}",
          client = client,
          type = QuestTypes.delivery,
          reward = mapOf(ResourceType.gold to 1000),
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
