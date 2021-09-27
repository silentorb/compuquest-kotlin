package scripts.gui

import godot.Node
import godot.TabContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class Management : Node() {

  private var tabContainer: TabContainer? = null

  @RegisterFunction
  override fun _ready() {
	tabContainer = findNode("tab-container") as? TabContainer
  }

  fun setActiveTab(screen: ManagementScreens) {
	tabContainer?.currentTab = screen.ordinal.toLong()
  }

  @RegisterFunction
  override fun _process(delta: Double) {
  }
}
