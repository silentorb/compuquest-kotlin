package compuquest.clienting.gui

import compuquest.simulation.general.World
import godot.Node
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

typealias ConversationEventSource = (World, Id) -> Events

data class MenuContent(
  val message: List<String> = listOf(),
  val items: List<MenuItem> = listOf(),
)

data class MenuAddress(
  val key: String,
  val argument: Any? = null,
)

typealias MenuStack = List<MenuAddress>

data class MenuItem(
  val title: String,
  val address: MenuAddress? = null,
  val events: ConversationEventSource? = null,
)

typealias TitleSource<Context> = (Context, Any?) -> String
typealias ContentSource<Context> = (Context, Any?) -> Node

fun staticTitle(title: String): TitleSource<Any> = { _, _ -> title }

data class Screen<Context>(
  val title: TitleSource<Context>,
  val content: ContentSource<Context>,
)

data class GameContext(
  val world: World,
  val actor: Id,
)

typealias GameScreen = Screen<GameContext>
