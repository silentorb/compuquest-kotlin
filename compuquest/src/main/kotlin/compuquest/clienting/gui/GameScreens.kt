package compuquest.clienting.gui

import compuquest.simulation.characters.AccessoryContainer
import compuquest.simulation.characters.Character
import compuquest.simulation.general.*
import scripts.gui.AccessoriesBrowser
import scripts.gui.Management
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event

fun newManagementMenu(screen: Key): Management {
	val control = instantiateScene<Management>("res://gui/menus/Management.tscn")!!
	control.setActiveTab(screen)
	return control
}

fun questManagementScreen() =
	GameScreen(
		title = staticTitle("Quests"),
		content = { _, _ -> newManagementMenu(Screens.manageQuests) }
	)

fun memberManagementScreen() =
	GameScreen(
		title = staticTitle("Members"),
		content = { _, _ -> newManagementMenu(Screens.manageMembers) }
	)

fun jobInterviewConversation() =
	GameScreen(
		title = staticTitle("Hiring"),
		content = { _, argument ->
			val other = argument as Id
			newConversationMenu(
				GameMenuContent(
					message = listOf("Hey, friend!  You wanna make more money?"),
					items = listOf(
						GameMenuItem(
							title = "Hire",
							events = { context ->
								val player = context.actor
								listOf(
									Event(hiredNpc, player, other),
									Event(joinedPlayer, other, player),
								)
							}
						)
					),
				)
			)
		}
	)

fun offerQuestsConversation() =
	GameScreen(
		title = staticTitle("Quest"),
		content = { context, argument ->
			val quest = argument as Id
			val deck = context.world.deck
			newConversationMenu(
				GameMenuContent(
					message = listOf("Will you ${formatQuestDescription(deck, deck.quests[quest]!!)}?"),
					items = listOf(
						GameMenuItem(
							title = "Accept",
							events = { context2 ->
								val player = context2.actor
								listOf(
									Event(setQuestHeroEvent, quest, player),
									Event(setQuestStatusEvent, quest, QuestStatus.active),
								)
							}
						),
					),
				)
			)
		}
	)

//fun shoppingConversation() =
//	GameScreen(
//		title = staticTitle("Purchase Something"),
//		content = { context, argument ->
//			val other = argument as Id
//			val wares = context.world.deck.wares.filterValues { it.owner == other }
//			newConversationMenu(
//				GameMenuContent(
//					message = listOf("What would you like to buy?"),
//					items = wares
//						.filterValues { it.quantity >= it.quantityPerPurchase }
//						.map { (ware, wareRecord) ->
//							val price = wareRecord.price.entries.first()
//							val purchasePrice = price.value * wareRecord.quantityPerPurchase
//							val resourceName = wareRecord.resourceType!!.name
//							val name = " ${wareRecord.quantityPerPurchase} $resourceName for $purchasePrice ${price.key} "
//							GameMenuItem(
//								title = name,
//								enabled = { context, _ ->
//									val player = context.actor
//									val deck = context.world.deck
////									val faction = deck.players[player]!!.faction
////									val balance = deck.factions[faction]!!.resources[price.key] ?: 0
////									balance >= purchasePrice
//									false
//								},
//								events = { context ->
//									val player = context.actor
//									purchase(context.world.deck, player, ware, wareRecord.quantityPerPurchase)
//								}
//							)
//						},
//				)
//			)
//		}
//	)

//fun completeQuestConversation() =
//	GameScreen(
//		title = staticTitle("Complete Quest"),
//		content = { _, argument ->
//			newConversationMenu(
//				GameMenuContent(
//					message = listOf("Thank you!"),
//					items = listOf(
//						GameMenuItem(
//							title = "Accept",
//							events = { context ->
//								val quest = argument as Id
//								val deck = context.world.deck
//								val faction = deck.players[context.actor]!!.faction
//								val questRecord = deck.quests[quest]!!
//								listOf(
//									Event(setQuestStatusEvent, quest, QuestStatus.completed),
//									modifyFactionResources(faction, questRecord.reward),
//								)
//							}
//						)
//					)
//				),
//			)
//		}
//	)

//fun resurrectMenuItem(actor: Id, character: Character) =
//  GameMenuItem(
//    title = "Resurrect ${character.name} ($100)",
////    key = MenuAddress("resurrect", actor),
//    events = { context ->
//      val deck = context.world.deck
//      val targetCharacter = deck.characters[actor]!!
//      val faction = targetCharacter.faction
//      listOf(
//        modifyHealth(actor, targetCharacter.definition.health),
//      ) + if (faction != Factions.neutral.name)
//        listOf(modifyFactionResources(faction, mapOf(ResourceType.gold to -100)))
//      else
//        listOf()
//    }
//  )

//fun resurrectionConversation() =
//  GameScreen(
//    title = staticTitle("Resurrection"),
//    content = { context, _ ->
//      val deck = context.world.deck
//      val party = deck.players[context.actor]!!.party
//      val items = party
//        .mapNotNull { id ->
//          val character = deck.characters[id]!!
//          if (character.isAlive)
//            null
//          else {
//            resurrectMenuItem(id, character)
//          }
//        }
//
//      val message = if (items.any())
//        listOf("Are there dead in need of rising?")
//      else
//        listOf("You can bring your dead here to be restored.")
//
//      newConversationMenu(
//        GameMenuContent(
//          message = message,
//          items = items,
//        )
//      )
//    }
//  )

fun getConversationOptions(deck: Deck, target: Id, targetCharacter: Character): List<MenuAddress> =
	listOfNotNull(
//    if (targetCharacter.attributes.contains("forHire"))
//      MenuAddress(Screens.jobInterview, target)
//    else
//      null,
//    if (hasAccessoryWithEffect(deck.accessories, target, AccessoryEffects.resurrect))
//      MenuAddress(Screens.resurrection, target)
//    else
//      null,
		if (deck.wares.any { it.value.owner == target })
			MenuAddress(Screens.shopping, target)
		else
			null,
	) + getAvailableQuests(deck, target).map {
		MenuAddress(Screens.offerQuest, it.key)
	} + readyToCompleteQuests(deck, target).map {
		MenuAddress(Screens.completeQuest, it.key)
	}

val leaveMenuItem =
	GameMenuItem(
		title = "Leave"
	)

fun emptyConversation() =
	newConversationMenu(
		GameMenuContent(
			message = listOf("This individual has nothing to say to you"),
			items = listOf(),
		)
	)

fun addressToGameScreen(context: GameContext, address: MenuAddress) =
	gameScreens[address.key]!!.content(context, address.argument)

fun conversationMenu() =
	GameScreen(
		title = { context, argument ->
			val name = context.world.deck.characters[argument as? Id]?.name ?: ""
			"Converse with $name"
		},
		content = { context, argument ->
			val deck = context.world.deck
			val target = argument as? Id
			val targetCharacter = deck.characters[target]
			if (target != null && targetCharacter != null) {
				val onInteracts = getConversationOptions(deck, target, targetCharacter)
				val items = onInteracts
					.map { address ->
						val screen = gameScreens[address.key]!!
						val title = screen.title(context, address.argument)
						GameMenuItem(
							title = title,
							address = address,
						)
					}

				if (items.any()) {
					if (items.size == 1 && items.first().address != null) {
						val address = items.first().address!!
						addressToGameScreen(context, address)
					} else
						newConversationMenu(
							GameMenuContent(
								message = listOf("What do you want?"),
								items = items,
							)
						)
				} else
					emptyConversation()
			} else
				emptyConversation()
		}
	)

fun newAccessoriesBrowser(deck: Deck, actor: Id): AccessoriesBrowser {
	val character = deck.characters[actor]!!
	val screen = instantiateScene<AccessoriesBrowser>("res://gui/menus/AccessoriesBrowser.tscn")!!
	screen.proficiencies = character.proficiencies
	return screen
}

fun getAccessoryDefinitions(container: AccessoryContainer) =
	container.accessories.mapValues { it.value.definition }.entries.toList()

fun characterInfoScreen() =
	GameScreen(
		title = staticTitle("Character Info"),
		content = { context, argument ->
			val deck = context.world.deck
			val container = deck.containers[context.actor]!!
			val screen = newAccessoriesBrowser(deck, context.actor)
			screen.accessoryLists = listOf(getAccessoryDefinitions(container))
			screen
		}
	)

fun equipNewCharacterScreen() =
	GameScreen(
		title = staticTitle("Equip Character"),
		content = { context, argument ->
			val deck = context.world.deck
			val container = deck.containers[context.actor]!!
			val screen = newAccessoriesBrowser(deck, context.actor)
			val ownedDefinitions = getAccessoryDefinitions(container)
			val options = context.world.definitions.accessories
				.filterValues { !it.isConsumable }
				.filterKeys { key -> ownedDefinitions.none { it.value.key == key } }
				.mapValues { it.value }.entries.toList()

			screen.accessoryLists = listOf(
				options,
				ownedDefinitions,
			)
			screen
		}
	)
