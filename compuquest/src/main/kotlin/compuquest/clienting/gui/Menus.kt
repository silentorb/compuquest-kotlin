package compuquest.clienting.gui

import compuquest.simulation.general.*
import godot.Node
import scripts.Global
import scripts.gui.*
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Event

fun launchMenu(slot: Node, menu: Node) {
  clearChildren(slot)
  slot.addChild(menu)
}

fun launchMenu(slot: Node, scenePath: String): Node? {
  val menu = instantiateScene<Node>(scenePath)
  return if (menu != null) {
    launchMenu(slot, menu)
    menu
  } else
    null
}

fun launchManagementMenu(slot: Node, menu: String) {
  val screen = stringToManagementScreen(menu)
  if (screen != null) {
    val control = slot.getChildren().firstOrNull() as? Management
      ?: launchMenu(slot, "res://gui/menus/Management.tscn") as? Management

    control?.setActiveTab(screen)
  }
}

fun newMenuScreen(content: MenuContent): MenuScreen {
  val screen = instantiateScene<MenuScreen>("res://gui/menus/MenuScreen.tscn")!!
  screen.content = content
  return screen
}

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
                  Event(setQuestHeroEvent, quest.key, player)
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

fun resurrectionConversation(other: Id) =
  MenuItem(
    title = "Resurrection",
    content = { _, _ ->
      MenuContent(
        message = "Has there been a murder?", // "Do you believe in the resurrection after death?
        items =
        listOf(
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

fun getConversationDefinition(onInteract: String, other: Id): MenuItem? =
  when (onInteract) {
    InteractionBehaviors.jobInterview -> jobInterviewConversation(other)
    InteractionBehaviors.offerQuests -> offerQuestsConversation(other)
    InteractionBehaviors.completeQuest -> completeQuestConversation(other)
    InteractionBehaviors.resurrect -> resurrectionConversation(other)
    else -> null
  }

fun manageMenu(slot: Node, actor: Id, player: Player?, menuStack: List<Key>): List<Key> {
  return tempCatch {
    val interactingWith = player?.interactingWith
    val menu = player?.menu
    val slotHasMenu = slot.getChildCount() > 0
    val topMenu = menuStack.lastOrNull()
    when {
      interactingWith != null -> {
        val world = Global.world!!
        val deck = world.deck
        val targetCharacter = deck.characters[interactingWith]
        if (!slotHasMenu && targetCharacter != null) {
          val onInteracts = getOnInteracts(deck, interactingWith, targetCharacter)
          val items = onInteracts.mapNotNull {
            getConversationDefinition(it, interactingWith)
          }

          if (items.any()) {
            val content = if (items.size == 1 && items.first().content != null)
              items.first().content!!(world, actor)
            else
              MenuContent(
                items = items
              )

            val screen = newMenuScreen(content)

            launchMenu(slot, screen)
          }
        }
        menuStack
      }
      menu != null -> {
        if (!slotHasMenu || menu != topMenu) {
          if (menu == gameOverScreen) {
            launchMenu(slot, showGameOverScreen())
          } else
            launchManagementMenu(slot, menu)
        }
        listOf(menu)
      }
      else -> {
        if (slotHasMenu)
          slot.getChild(0)?.queueFree()

        menuStack
      }
    }
  }!!
}
