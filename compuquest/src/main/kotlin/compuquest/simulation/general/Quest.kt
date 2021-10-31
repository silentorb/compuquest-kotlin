package compuquest.simulation.general

import compuquest.simulation.definition.ResourceType
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

enum class QuestStatus {
  available,
  active,
  completed,
  failed,
}

data class Quest(
  val client: Id,
  val hero: Id? = null,
  val status: QuestStatus = QuestStatus.available,
  val name: String,
  val type: Key,
  val reward: ResourceMap,
  val duration: Int = 0, // Seconds
  val elapsedFrames: Int = 0,
  val recipientName: Key? = null, // For delivery quests
  val recipient: Id? = null, // For delivery quests
  val penaltyValue: Int = 0,
)

object QuestTypes {
  const val delivery = "delivery"
}

fun getAvailableQuests(deck: Deck, actor: Id): Sequence<Map.Entry<Id, Quest>> =
  deck.quests
    .asSequence()
    .filter { it.value.client == actor && it.value.hero == null }

fun readyToCompleteQuests(deck: Deck, target: Id): Sequence<Map.Entry<Id, Quest>> =
  deck.quests
    .asSequence()
    .filter { it.value.recipient == target && it.value.status == QuestStatus.active }

const val setQuestHeroEvent = "setQuestHero"
const val setQuestStatusEvent = "setQuestStatus"

fun isQuestOver(quest: Quest): Boolean =
  quest.status == QuestStatus.completed || quest.status == QuestStatus.failed

fun eventsFromQuests(deck: Deck): Events =
  deck.quests.flatMap { (id, quest) ->
    when {
      !isQuestOver(quest) && quest.duration > 0 && quest.elapsedFrames > quest.duration * 60 -> {
        listOfNotNull(
          Event(setQuestStatusEvent, id, QuestStatus.failed),
          if (quest.hero != null && quest.penaltyValue > 0) {
            val faction = deck.players[quest.hero]!!.faction
            modifyFactionResources(faction, mapOf(ResourceType.gold to -quest.penaltyValue))
          } else
            null
        )
      }
      else -> listOf()
    }
  }

fun updateQuest(events: Events): (Id, Quest) -> Quest = { id, quest ->
  val hero = events
    .firstOrNull { it.type == setQuestHeroEvent && it.target == id }
    ?.value as? Id
    ?: quest.hero

  val status = events
    .firstOrNull { it.type == setQuestStatusEvent && it.target == id }
    ?.value as? QuestStatus
    ?: quest.status

  val elapsedFrames =
    if (!isQuestOver(quest) && quest.duration != 0)
      quest.elapsedFrames + 1
    else
      0

  quest.copy(
    hero = hero,
    status = status,
    elapsedFrames = elapsedFrames,
  )
}
