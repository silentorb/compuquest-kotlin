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

data class MenuAddress(
  val key: String,
  val context: Any? = null,
)

data class MenuItem(
  val title: String,
  val key: Any = title,
  val content: ConversationContentSource? = null,
  val events: ConversationEventSource? = null,
)
