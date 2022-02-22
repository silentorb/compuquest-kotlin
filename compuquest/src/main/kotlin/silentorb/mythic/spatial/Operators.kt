package silentorb.mythic.spatial

import godot.core.Vector2
import godot.core.Vector3

fun Vector3.xy(): Vector2 = Vector2(x, y)
