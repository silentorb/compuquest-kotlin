package scripts.gui

import compuquest.clienting.gui.managementScreens
import godot.Node
import godot.TabContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import silentorb.mythic.ent.Key

@RegisterClass
class Management : Node() {

  private var tabContainer: TabContainer? = null
  var startingIndex: Int = 0

  @RegisterFunction
  override fun _ready() {
	tabContainer = findNode("tab-container") as? TabContainer
	tabContainer?.currentTab = startingIndex.toLong()
  }

  fun setActiveTab(screen: Key) {
	val index = managementScreens.indexOf(screen)
	val localContainer = tabContainer
	if (localContainer != null) {
	  tabContainer?.currentTab = index.toLong()
	} else {
	  startingIndex = index
	}
  }

  @RegisterFunction
  override fun _process(delta: Double) {
  }
}
