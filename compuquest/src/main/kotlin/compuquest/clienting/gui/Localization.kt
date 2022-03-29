package compuquest.clienting.gui

import silentorb.mythic.localization.DevText
import silentorb.mythic.localization.Text

fun resolveText(text: Text): String =
	when (text) {
		is DevText -> text.value
		else -> throw Error("Unsupported Text type")
	}
