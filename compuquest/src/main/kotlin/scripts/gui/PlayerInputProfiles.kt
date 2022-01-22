package scripts.gui

import compuquest.clienting.gui.navigateBack
import compuquest.simulation.input.Commands
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.setPlayerInputProfiles
import silentorb.mythic.happening.newEvent

@RegisterClass
class PlayerInputProfiles : Node() {

	var profiles: List<Int> = listOf()
	var state: MutableList<Int> = mutableListOf(0, 0, 0, 0)
	var actor: Id = 0L

	@RegisterFunction
	fun on_changed(profileIndex: Int, playerIndex: Int) {
		state[playerIndex] = profiles[profileIndex]
	}

	@RegisterFunction
	fun on_submit() {
		Global.addEvent(newEvent(setPlayerInputProfiles, value = state.toList()))
		navigateBack(actor)
	}

	@RegisterFunction
	fun on_cancel() {
		navigateBack(actor)
	}

	@RegisterFunction
	override fun _ready() {
		val grid = findNode("grid") as GridContainer
		val okay = findNode("okay") as Button
		val cancel = findNode("cancel") as Button
		val client = Global.instance!!.client!!
		val profiles = client.input.profiles.entries
		this.profiles = profiles.map { it.key }
		for (index in (0..3)) {
			val label = Label()
			label.text = "Player ${index + 1}"
			grid.addChild(label)

			val dropdown = OptionButton()
			for ((_, profile) in profiles) {
				dropdown.addItem(profile.name)
			}
			val value = client.input.playerProfiles[index]
			state[index] = value
			dropdown.select(this.profiles.indexOf(value).toLong())
			dropdown.connect("item_selected", this, "on_changed", variantArrayOf(index))
			grid.addChild(dropdown)
		}
		okay.connect("pressed", this, "on_submit")
		cancel.connect("pressed", this, "on_cancel")
	}
}
