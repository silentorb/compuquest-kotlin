package scripts.gui

import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

typealias ConversationEventSource = (World, Id, Id) -> Events

data class ConversationOption(
  val title: String,
  val events: ConversationEventSource? = null,
)

data class ConversationDefinition(
  val message: String,
  val options: List<ConversationOption>,
)
