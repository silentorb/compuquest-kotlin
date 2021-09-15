package scripts.gui

import compuquest.simulation.general.Character
import compuquest.simulation.general.displayText
import godot.AnimatedSprite
import godot.Container
import godot.Label
import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import scripts.Global
import silentorb.mythic.ent.Id

@RegisterClass
class Portrait : Node() {

  private var lastCharacter: Character? = null

  @Export
  @RegisterProperty
  var index: Int = 0

  private var avatar: AnimatedSprite? = null
  private var verticalBox: Container? = null
  private var nameLabel: Label? = null
  private var health: Label? = null

  @RegisterFunction
  override fun _ready() {
	avatar = findNode("avatar") as AnimatedSprite
	verticalBox = findNode("vbox") as Container
	nameLabel = findNode("name") as Label
	health = findNode("health") as Label
	verticalBox?.visible = false
  }

  fun characterChanged(character: Character) {
//    avatar?.frame = character.frame
	if (character.depiction != "")
	  avatar?.animation = character.depiction

	nameLabel?.text = character.name
	health?.text = displayText(character.health)
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	val world = Global.world
	val localCharacter = lastCharacter
	val deck = world?.deck
	val actor = deck?.players?.values?.firstOrNull()?.party?.getOrNull(index)
	if (actor == null) {
	  lastCharacter = null
	} else {
	  val character = deck.characters[actor]
	  if (localCharacter != character) {
		lastCharacter = character
		if (character != null)
		  characterChanged(character)
	  }
	}
	verticalBox?.visible = lastCharacter != null
  }
}
