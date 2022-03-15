package compuquest.population

import godot.core.Vector3

// This is a primitive method but relatively fast and works for cases where even distribution is not needed
tailrec fun removeClumps(range: Float, nodes: List<Vector3>, removed: List<Vector3> = listOf()): List<Vector3> =
    if (nodes.none())
      removed
    else {
      val location = nodes.first()
      val remaining = nodes.drop(1)
      val tooClose = remaining
          .filter { it.distanceTo(location) < range }

      removeClumps(range, remaining - tooClose, removed + tooClose)
    }
