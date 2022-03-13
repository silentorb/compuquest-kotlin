package compuquest.generation.general

fun rotateSides(turns: Int): (SideMap) -> SideMap = { sides ->
  sides
      .mapKeys { rotateY(turns, it.key) }
}
