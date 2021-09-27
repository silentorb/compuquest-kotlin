package compuquest.population

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.ContractDefinition
import compuquest.simulation.general.QuestTypes
import compuquest.simulation.general.World

fun populateQuests(world: World): World {
  val deck = world.deck
  val nextId = world.nextId.source()
  val dice = world.dice
  val clients = deck.characters.filter { it.value.isClient }
  val withAvailableContracts = clients
    .mapValues { (client, character) ->
      val recipient = dice.takeOneOrNull(clients.keys.minus(client))
      if (recipient != null)
        character.copy(
          availableContracts = mapOf(
            nextId() to ContractDefinition(
              type = QuestTypes.delivery,
              reward = mapOf(ResourceType.gold to 1000),
              recipient = recipient,
            ),
          )
        )
      else
        character
    }

  return world.copy(
    deck = deck.copy(
      characters = deck.characters + withAvailableContracts,
    )
  )
}
