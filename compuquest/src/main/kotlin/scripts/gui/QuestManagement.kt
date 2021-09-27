package scripts.gui

import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class QuestManagement : Node() {

  @RegisterFunction
  override fun _ready() {
    val world = Global.world
    val player = Global.getPlayer()
    if (world != null && player != null) {
      tempCatch {
        val questList = findNode("quests")!!
        val quests = world.deck.quests.filter { it.value.hero == player.key }
        for (quest in quests) {
          val nameLabel = Label()
          nameLabel.text = quest.value.name
          val statusLabel = Label()
          statusLabel.text = quest.value.status.toString().capitalize()
          questList.addChild(nameLabel)
          questList.addChild(statusLabel)
        }
      }
    }
  }
}
