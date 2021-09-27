package scripts.gui

import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.*
import godot.Node
import scripts.Global
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event

var lastMenu: String? = null

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

fun jobInterviewConversation() =
  ConversationDefinition(
    message = "Hey, friend!  You wanna make more money?",
    options = listOf(
      ConversationOption(
        title = "Hire",
        events = { _, player, other ->
          listOf(
            Event(hiredNpc, player, other),
            Event(joinedPlayer, other, player),
          )
        }
      ),
      ConversationOption(
        title = "Leave",
      ),
    ),
  )

fun offerQuestsConversation() =
  ConversationDefinition(
    message = "I have a job for you.",
    options = listOf(
      ConversationOption(
        title = "Accept",
        events = { world, player, other ->
          val quest = getAvailableQuests(world.deck, other).firstOrNull()
          if (quest != null)
            listOf(
              Event(setQuestHeroEvent, quest.key, player)
            )
          else
            listOf()
        }
      ),
      ConversationOption(
        title = "Leave",
      ),
    ),
  )

fun completeQuestConversation() =
  ConversationDefinition(
    message = "Thank you!",
    options = listOf(
      ConversationOption(
        title = "Accept",
        events = { world, player, other ->
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
      ),
    ),
  )

fun getConversationDefinition(onInteract: String): ConversationDefinition? =
  when (onInteract) {
    InteractionBehaviors.jobInterview -> jobInterviewConversation()
    InteractionBehaviors.offerQuests -> offerQuestsConversation()
    InteractionBehaviors.completeQuest -> completeQuestConversation()
    else -> null
  }

fun manageMenu(slot: Node, player: Player?) {
  val interactingWith = player?.interactingWith
  val menu = player?.menu
  val slotHasMenu = slot.getChildCount() > 0
  if (interactingWith != null) {
    val world = Global.world!!
    val deck = world.deck
    val targetCharacter = deck.characters[interactingWith]
    if (!slotHasMenu && targetCharacter != null) {
      val onInteract = getOnInteract(deck, interactingWith, targetCharacter)
      val definition = if (onInteract != null)
        getConversationDefinition(onInteract)
      else
        null

      if (definition != null) {
        val screen = instantiateScene<Conversation>("res://gui/menus/Conversation.tscn")!!
        screen.definition = definition
        launchMenu(slot, screen)
      }
    }
  } else if (menu != null) {
    if (!slotHasMenu || menu != lastMenu) {
      if (menu == gameOverScreen) {
        launchMenu(slot, showGameOverScreen())
      } else
        launchManagementMenu(slot, menu)
    }
  } else {
    if (slotHasMenu)
      slot.getChild(0)?.queueFree()
  }

  lastMenu = menu
}
