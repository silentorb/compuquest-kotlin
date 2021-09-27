package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events

enum class QuestStatus {
  active,
  completed,
  failed,
}

data class Quest(
  val client: Id,
  val hero: Id? = null,
  val status: QuestStatus = QuestStatus.active,
  val type: Key,
  val reward: ResourceMap,
  val recipient: Key? = null, // For delivery quests
)

object QuestTypes {
  const val delivery = "delivery"
}

fun getAvailableQuests(deck: Deck, actor: Id): Sequence<Map.Entry<Id, Quest>> =
  deck.quests
    .asSequence()
    .filter { it.value.client == actor && it.value.hero == null }

fun readyToCompleteQuests(deck: Deck, target: Character): Sequence<Map.Entry<Id, Quest>> =
  deck.quests
    .asSequence()
    .filter { target.key != null && it.value.recipient == target.key && it.value.status == QuestStatus.active }

const val setQuestHeroEvent = "setQuestHero"
const val setQuestStatusEvent = "setQuestStatus"

fun updateQuest(events: Events): (Id, Quest) -> Quest = { id, quest ->
  val hero = events
    .firstOrNull { it.type == setQuestHeroEvent && it.target == id }
    ?.value as? Id
    ?: quest.hero

  val status = events
    .firstOrNull { it.type == setQuestStatusEvent && it.target == id }
    ?.value as? QuestStatus
    ?: quest.status

  quest.copy(
    hero = hero,
    status = status
  )
}
