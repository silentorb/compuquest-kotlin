package compuquest.clienting.gui

import compuquest.simulation.general.*
import godot.Node
import scripts.gui.Management
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event

object Screens {
  const val conversation = "conversation"
  const val jobInterview = "jobInterview"
  const val offerQuest = "offerQuest"
  const val completeQuest = "completeQuest"
  const val resurrection = "resurrection"
  const val manageQuests = "manageQuests"
  const val manageMembers = "manageMembers"
}

val managementScreens = listOf(
  Screens.manageMembers,
  Screens.manageQuests,
)

fun newManagementMenuOld(slot: Node, screen: Key): Management? {
  val existing = slot.getChildren().firstOrNull() as? Management
  val control = existing
    ?: mountScreen(slot, "res://gui/menus/Management.tscn") as? Management

  control?.setActiveTab(screen)
  return if (existing == null)
    control
  else
    null
}

fun newManagementMenu(screen: Key): Management {
  val control = instantiateScene<Management>("res://gui/menus/Management.tscn")!!
  control.setActiveTab(screen)
  return control
}

fun questManagementScreen() =
  GameScreen(
    title = staticTitle("Quests"),
    content = { _, _ -> newManagementMenu(Screens.manageQuests) }
  )

fun memberManagementScreen() =
  GameScreen(
    title = staticTitle("Members"),
    content = { _, _ -> newManagementMenu(Screens.manageMembers) }
  )

fun jobInterviewConversation() =
  GameScreen(
    title = staticTitle("Hiring"),
    content = { _, argument ->
      val other = argument as Id
      newConversationMenu(
        MenuContent(
          message = listOf("Hey, friend!  You wanna make more money?"),
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
      )
    }
  )

fun offerQuestsConversation() =
  GameScreen(
    title = staticTitle("Quest"),
    content = { _, argument ->
      val quest = argument as Id
      newConversationMenu(
        MenuContent(
          message = listOf("I have a quest for you."),
          items = listOf(
            MenuItem(
              title = "Accept",
              events = { _, player ->
                listOf(
                  Event(setQuestHeroEvent, quest, player),
                  Event(setQuestStatusEvent, quest, QuestStatus.active),
                )
              }
            ),
          ),
        )
      )
    }
  )

fun completeQuestConversation() =
  GameScreen(
    title = staticTitle("Complete Quest"),
    content = { _, argument ->
      newConversationMenu(
        MenuContent(
          message = listOf("Thank you!"),
          items = listOf(
            MenuItem(
              title = "Accept",
              events = { world, player ->
                val quest = argument as Id
                val deck = world.deck
                val faction = deck.players[player]!!.faction
                val questRecord = deck.quests[quest]!!
                listOf(
                  Event(setQuestStatusEvent, quest, QuestStatus.completed),
                  Event(modifyFactionResourcesEvent, faction, questRecord.reward),
                )
              }
            )
          )
        ),
      )
    }
  )

fun resurrectMenuItem(actor: Id, character: Character) =
  MenuItem(
    title = "Resurrect ${character.name}",
//    key = MenuAddress("resurrect", actor),
    events = { world, _ ->
      val deck = world.deck
      val targetCharacter = deck.characters[actor]!!
      listOf(
        modifyHealth(actor, targetCharacter.health.max),
      )
    }
  )

fun resurrectionConversation() =
  GameScreen(
    title = staticTitle("Resurrection"),
    content = { context, _ ->
      val deck = context.world.deck
      val party = deck.players[context.actor]!!.party
      val items = party
        .mapNotNull { id ->
          val character = deck.characters[id]!!
          if (character.isAlive)
            null
          else {
            resurrectMenuItem(id, character)
          }
        }

      val message = if (items.any())
        listOf("Are there dead in need of rising?")
      else
        listOf("You can bring your dead here to be restored.")

      newConversationMenu(
        MenuContent(
          message = message,
          items = items,
        )
      )
    }
  )

fun getConversationOptions(deck: Deck, target: Id, targetCharacter: Character): List<MenuAddress> =
  listOfNotNull(
    if (targetCharacter.attributes.contains("forHire"))
      MenuAddress(Screens.jobInterview, target)
    else
      null,
    if (hasAccessoryWithEffect(deck.accessories, target, AccessoryEffects.resurrect))
      MenuAddress(Screens.resurrection, target)
    else
      null,
  ) + getAvailableQuests(deck, target).map {
    MenuAddress(Screens.offerQuest, it.key)
  } + readyToCompleteQuests(deck, targetCharacter).map {
    MenuAddress(Screens.completeQuest, it.key)
  }

val leaveMenuItem =
  MenuItem(
    title = "Leave"
  )

fun emptyConversation() =
  newConversationMenu(
    MenuContent(
      message = listOf("This individual has nothing to say to you"),
      items = listOf(),
    )
  )

fun addressToGameScreen(context: GameContext, address: MenuAddress) =
  gameScreens[address.key]!!.content(context, address.argument)

fun conversationMenu() =
  GameScreen(
    title = { context, argument ->
      val name = context.world.deck.characters[argument as? Id]?.name ?: ""
      "Converse with $name"
    },
    content = { context, argument ->
      val deck = context.world.deck
      val target = argument as? Id
      val targetCharacter = deck.characters[target]
      if (target != null && targetCharacter != null) {
        val onInteracts = getConversationOptions(deck, target, targetCharacter)
        val items = onInteracts
          .map { address ->
            val screen = gameScreens[address.key]!!
            val title = screen.title(context, address.argument)
            MenuItem(
              title = title,
              address = address,
            )
          }

        if (items.any()) {
          if (items.size == 1 && items.first().address != null) {
            val address = items.first().address!!
            addressToGameScreen(context, address)
          } else
            newConversationMenu(
              MenuContent(
                message = listOf("What do you want?"),
                items = items,
              )
            )
        } else
          emptyConversation()
      } else
        emptyConversation()
    }
  )

val gameScreens: Map<Key, GameScreen> = mapOf(
  Screens.completeQuest to completeQuestConversation(),
  Screens.jobInterview to jobInterviewConversation(),
  Screens.resurrection to resurrectionConversation(),
  Screens.conversation to conversationMenu(),
  Screens.offerQuest to offerQuestsConversation(),
  Screens.manageQuests to questManagementScreen(),
  Screens.manageMembers to memberManagementScreen(),
)
