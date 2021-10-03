package compuquest.clienting.gui

import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

typealias ConversationEventSource = (World, Id) -> Events
typealias ConversationContentSource = (World, Id) -> MenuContent

data class MenuContent(
  val message: String = "",
  val items: List<MenuItem> = listOf(),
)

data class MenuItem(
  val title: String,
  val content: ConversationContentSource? = null,
  val events: ConversationEventSource? = null,
)
