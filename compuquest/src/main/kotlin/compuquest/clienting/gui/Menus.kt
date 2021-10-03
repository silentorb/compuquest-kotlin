package compuquest.clienting.gui

import compuquest.simulation.general.*
import scripts.Global
import scripts.gui.*
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event

fun jobInterviewConversation(other: Id) =
  MenuItem(
    title = "Hiring",
    content = { _, _ ->
      MenuContent(
        message = "Hey, friend!  You wanna make more money?",
        items = listOf(
          MenuItem(
            title = "Hire",
            events = { _, player ->
              listOf(
                Event(hiredNpc, player, other),
                Event(joinedPlayer, other, player),
              )
            }
          )
        ),
      )
    }
  )

fun offerQuestsConversation(other: Id) =
  MenuItem(
    title = "Quest",
    content = { _, _ ->
      MenuContent(
        message = "I have a quest for you.",
        items = listOf(
          MenuItem(
            title = "Accept",
            events = { world, player ->
              val quest = getAvailableQuests(world.deck, other).firstOrNull()
              if (quest != null)
                listOf(
                  Event(setQuestHeroEvent, quest.key, player),
                  Event(setQuestStatusEvent, quest.key, QuestStatus.active),
                )
              else
                listOf()
            }
          ),
        ),
      )
    }
  )

fun completeQuestConversation(other: Id) =
  MenuItem(
    title = "Complete Quest",
    content = { _, _ ->
      MenuContent(
        message = "Thank you!",
        items = listOf(
          MenuItem(
            title = "Accept",
            events = { world, player ->
              val deck = world.deck
              val targetCharacter = deck.characters[other]!!
              val faction = deck.players[player]!!.faction

              readyToCompleteQuests(deck, targetCharacter)
                .flatMap { quest ->
                  listOf(
                    Event(setQuestStatusEvent, quest.key, QuestStatus.completed),
                    Event(modifyFactionResourcesEvent, faction, quest.value.reward),
                  )
                }
                .toList()
            }
          )
        ),
      )
    }
  )

fun resurrectMenuItem(actor: Id, character: Character) =
  MenuItem(
    title = "Resurrect ${character.name}",
    key = MenuAddress("resurrect", actor),
    events = { world, _ ->
      val deck = world.deck
      val targetCharacter = deck.characters[actor]!!
      listOf(
        modifyHealth(actor, targetCharacter.health.max),
      )
    }
  )

fun resurrectionConversation(other: Id) =
  MenuItem(
    title = "Resurrection",
    content = { world, actor ->
      val deck = world.deck
      val party = deck.players[actor]!!.party
      val items = party
        .mapNotNull { id ->
          val character = deck.characters[id]!!
          if (character.isAlive)
            null
          else {
            resurrectMenuItem(id, character)
          }
        }

      MenuContent(
        message = "Has there been a murder?",
        items = items,
      )
    }
  )

fun getConversationDefinition(onInteract: String, other: Id): MenuItem? =
  when (onInteract) {
    InteractionBehaviors.jobInterview -> jobInterviewConversation(other)
    InteractionBehaviors.offerQuests -> offerQuestsConversation(other)
    InteractionBehaviors.completeQuest -> completeQuestConversation(other)
    InteractionBehaviors.resurrect -> resurrectionConversation(other)
    else -> null
  }

fun conversationMenu(actor: Id, target: Id?): MenuScreen? {
  val world = Global.world!!
  val deck = world.deck
  val targetCharacter = deck.characters[target]
  return if (target != null && targetCharacter != null) {
    val onInteracts = getOnInteracts(deck, target, targetCharacter)
    val items = onInteracts.mapNotNull {
      getConversationDefinition(it, target)
    }

    if (items.any()) {
      val content = if (items.size == 1 && items.first().content != null)
        items.first().content!!(world, actor)
      else
        MenuContent(
          items = items
        )

      newMenuScreen(content)
    }
    else
      null
  }
  else
    null
}
