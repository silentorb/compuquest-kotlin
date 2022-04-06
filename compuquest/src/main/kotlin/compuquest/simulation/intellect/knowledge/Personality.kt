package compuquest.simulation.intellect.knowledge

enum class Roaming {
	none,
	roaming,
	roamWhenAlerted,
}

data class Personality(
	val roaming: Roaming = Roaming.none,
)
