package scripts.gui

import compuquest.simulation.general.formatQuestDescription
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.tempCatch

//var selectedQuest: Id? = null

@RegisterClass
class QuestManagement : Node() {

  fun on_pressed(index: Long) {
//	selectedQuest = index
  }

  @RegisterFunction
  override fun _ready() {
	val world = Global.world
	val player = Global.getPlayer()
	if (world != null && player != null) {
	  tempCatch {
		val questList = findNode("quests")!!
		val quests = world.deck.quests.filter { it.value.hero == player.key }
//		val lastSelected = selectedQuest
//		val selected = if (quests.containsKey(lastSelected))
//		  lastSelected
//		else
//		  quests.keys.firstOrNull()

		for (quest in quests) {
			val description = formatQuestDescription(world.deck, quest.value).capitalize()
		  questList.addChild(newLabel(quest.value.name))
		  questList.addChild(newLabel(quest.value.status.toString().capitalize()))
		  questList.addChild(newLabel(description))
		}
	  }
	}
  }
}
