package scripts.gui

import compuquest.clienting.gui.managementScreens
import compuquest.simulation.general.*
import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Event

@RegisterClass
class Portrait : Node() {

  private var lastActor: Id? = null
  private var lastCharacter: Character? = null
  private var isManaging: Boolean = false

  @Export
  @RegisterProperty
  var index: Int = 0

  var member: Id? = null

  private var avatar: AnimatedSprite? = null
  private var verticalBox: Container? = null
  private var nameLabel: Label? = null
  private var health: ValueMax? = null
  private var moveButton: Button? = null
  private var fireButton: Button? = null

  @RegisterFunction
  override fun _ready() {
	tempCatch {
	  avatar = findNode("avatar") as AnimatedSprite
	  verticalBox = findNode("vbox") as Container
	  nameLabel = findNode("name") as Label
	  health = findNode("health") as ValueMax
	  moveButton = findNode("move") as Button
	  fireButton = findNode("fire") as Button
	  verticalBox?.visible = false
	  moveButton?.visible = false
	  fireButton?.visible = false
	  moveButton?.connect("pressed", this, "on_move_pressed")
	  fireButton?.connect("pressed", this, "on_fire_pressed")
	}
  }

  @RegisterFunction
  fun on_move_pressed() {
	val actor = lastActor
	if (actor != null) {
	  val player = Global.getPlayer()!!
	  val eventType = if (player.value.party.contains(actor))
		removeMemberFromParty
	  else
		addMemberToParty

	  Global.addPlayerEvent(eventType, actor)
	}
  }

  @RegisterFunction
  fun on_fire_pressed() {
	val actor = lastActor
	if (actor != null) {
	  Global.addEvent(Event(removeFactionMemberEvent, actor))
	}
  }

  fun characterChanged(character: Character, previous: Character?) {
//    avatar?.frame = character.frame
	tempCatch {
	  if (character.depiction != "")
		avatar?.animation = character.depiction

	  nameLabel?.text = character.name
	  val localHealth = health
	  if (localHealth != null) {
		localHealth.value = character.health.value
		localHealth.max = character.health.max
	  }
	}
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	tempCatch {
	  val world = Global.world
	  val deck = world?.deck
	  val player = getPlayer(world)?.value
	  val actor = member ?: player?.party?.getOrNull(index)
	  if (actor == null) {
		lastActor = null
		lastCharacter = null
	  } else {
		val localCharacter = lastCharacter
		val character = deck!!.characters[actor]
		if (localCharacter != character) {
		  if (character != null)
			characterChanged(character, localCharacter)

		  lastActor = actor
		  lastCharacter = character
		}
	  }
	  verticalBox?.visible = lastCharacter != null

	  val menuKey = Global.getMenuStack().lastOrNull()?.key

	  val currentIsManaging = player != null
		  && managementScreens.contains(menuKey)
		  && lastCharacter != null &&
		  (player.party.size > 1 || !player.party.contains(actor))

	  if (isManaging != currentIsManaging) {
		isManaging = currentIsManaging
		moveButton?.visible =
		  currentIsManaging && player != null && (player.party.size < maxPartySize || player.party.contains(actor))
		fireButton?.visible = currentIsManaging
	  }
	}
  }
}
