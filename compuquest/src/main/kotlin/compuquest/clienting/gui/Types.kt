package compuquest.clienting.gui

import compuquest.simulation.general.World
import godot.Node
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

typealias ConversationEventSource<Context> = (Context) -> Events

data class MenuContent<Context>(
  val message: List<String> = listOf(),
  val items: List<MenuItem<Context>> = listOf(),
)

data class MenuAddress(
  val key: String,
  val argument: Any? = null,
)

typealias MenuStack = List<MenuAddress>

data class MenuItem<Context>(
  val title: String,
  val address: MenuAddress? = null,
  val enabled: IsValid<Context>? = null,
  val events: ConversationEventSource<Context>? = null,
)

typealias TitleSource<Context> = (Context, Any?) -> String
typealias ContentSource<Context> = (Context, Any?) -> Node
typealias IsValid<Context> = (Context, Any?) -> List<String>?
typealias GameMenuItem = MenuItem<GameContext>
typealias GameMenuContent = MenuContent<GameContext>

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
