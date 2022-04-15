package scripts.entities

import godot.AudioStreamPlayer3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import silentorb.mythic.godoting.UnparentOnParentDeletion

@RegisterClass
class AudioPlayer : AudioStreamPlayer3D(), UnparentOnParentDeletion
