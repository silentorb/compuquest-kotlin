package compuquest.clienting.gui

import godot.Node
import scripts.gui.Hud
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene

fun addHud(parent: Node, actor: Id): Hud {
	val hud = instantiateScene<Node>("res://gui/hud/Hud.tscn")!! as Hud
	hud.actor = actor
	parent.addChild(hud)
	return hud
}

//	hud.owner = getTree()!!.editedSceneRoot
//	getParent()!!.callDeferred("add_child", hud)
