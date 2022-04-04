package scripts.entities

import godot.AudioStreamPlayer3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import silentorb.mythic.godoting.UnparentOnParentDeletion

@RegisterClass
class AudioPlayer : AudioStreamPlayer3D(), UnparentOnParentDeletion {
//
//	@RegisterFunction
//	fun on_exiting() {
//		val parent = getParent()
//		if (parent != null) {
//			val j = isQueuedForDeletion()
////			parent.removeChild(this)
//			parent.callDeferred("remove_child", this)
//		}
//	}
//
//	@RegisterFunction
//	fun on_exited() {
//		val parent = getParent()
//		val k = 0
//	}
//
//	@RegisterFunction
//	override fun _ready() {
//		connect("tree_exiting", this, "on_exiting")
//	}
//
//	override fun _onDestroy() {
//		val k = 0
//	}
}
