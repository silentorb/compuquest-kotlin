package scripts.gui

import godot.Engine
import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath

@RegisterClass
class WrapperContainer : Node() {

	@Export
	@RegisterProperty
	var oldParent: NodePath? = null

	@Export
	@RegisterProperty
	var newParent: NodePath? = null

	var moved = false

	// Would use _ready except that it can't move the child when called from a child node,
	// and creates a composition bottleneck when this script placed on the parent.
	// Instead this script is designed to be placed on a disposable node intended just for a one-time
	// moving of the child node.
	// Godot's node system is incredibly poorly designed.
	@RegisterFunction
	override fun _process(delta: Double) {
		if (!Engine.editorHint && !moved) {
			val previous = getNode(oldParent!!)!!
			val next = getNode(newParent!!)!!
			val child = previous.getChildren().filterIsInstance<Node>().last()
			previous.removeChild(child)
			next.addChild(child)
			queueFree()
			moved = true
		}
	}
}
