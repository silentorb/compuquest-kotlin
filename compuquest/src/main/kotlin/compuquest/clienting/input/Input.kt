package compuquest.clienting.input

import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.InputProfile

const val defaultInputProfile: Int = 1

fun defaultInputProfiles() =
	mapOf(
		defaultInputProfile to InputProfile(bindings = defaultInputProfile())
	)
