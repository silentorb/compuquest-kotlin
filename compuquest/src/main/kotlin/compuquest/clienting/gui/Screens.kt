package compuquest.clienting.gui

import silentorb.mythic.ent.Key

object Screens {
	const val conversation = "conversation"
	const val jobInterview = "jobInterview"
	const val offerQuest = "offerQuest"
	const val completeQuest = "completeQuest"
	const val manageQuests = "manageQuests"
	const val manageMembers = "manageMembers"
	const val shopping = "shopping"
	const val characterInfo = "characterInfo"
	const val equipCharacter = "equipCharacter"

	const val mainMenu = "mainMenu"
	const val options = "options"
	const val optionsAudio = "optionsAudio"
	const val optionsInput = "optionsInput"
	const val optionsInputProfiles = "optionsInputProfiles"
	const val optionsInputPlayerProfiles = "optionsInputPlayerProfiles"
	const val optionsDisplay = "optionsDisplay"

	const val chooseProfession = "chooseProfession"
}

val managementScreens = listOf(
	Screens.manageMembers,
	Screens.manageQuests,
)

val gameScreens: Map<Key, GameScreen> = mapOf(
	// App
	Screens.mainMenu to mainMenu(),
	Screens.options to optionsMenu(),
	Screens.optionsInput to optionsInputMenu(),
	Screens.optionsInputProfiles to optionsInputProfilesMenu(),
	Screens.optionsInputPlayerProfiles to optionsInputPlayerProfilesMenu(),
	Screens.chooseProfession to chooseProfessionMenu(),

	// Game
	Screens.characterInfo to characterInfoScreen(),
	Screens.equipCharacter to equipNewCharacterScreen(),

	// Old
//	Screens.completeQuest to completeQuestConversation(),
	Screens.jobInterview to jobInterviewConversation(),
	Screens.conversation to conversationMenu(),
	Screens.offerQuest to offerQuestsConversation(),
	Screens.manageQuests to questManagementScreen(),
	Screens.manageMembers to memberManagementScreen(),
//	Screens.shopping to shoppingConversation(),
)

// Screens that cannot be cancelled out of and require a selection once visited
val uncancellableScreens = setOf(
	Screens.chooseProfession,
)
