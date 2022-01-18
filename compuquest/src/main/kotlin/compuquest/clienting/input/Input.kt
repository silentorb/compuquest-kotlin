package compuquest.clienting.input

import silentorb.mythic.haft.Bindings

data class InputProfile(
  val bindings: Bindings,
)

const val defaultInputProfile: Int = 1

data class InputOptions(
  val profiles: Map<Int, InputProfile> = mapOf(),
  val playerProfiles: Map<Long, Int>
)
