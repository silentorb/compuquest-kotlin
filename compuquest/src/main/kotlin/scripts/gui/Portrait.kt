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

  fun characterChanged(character: Character, previous: Character?) {
//    avatar?.frame = character.frame
	if (character.depiction != "")
	  avatar?.animation = character.depiction

	nameLabel?.text = character.name
	val localHealth = health
	if (localHealth != null) {
	  localHealth.text = displayText(character.health)
	  numberChangedEffect(localHealth, character.health.value, previous?.health?.value)
	}
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	val world = Global.world
	val deck = world?.deck
	val actor = deck?.players?.values?.firstOrNull()?.party?.getOrNull(index)
	if (actor == null) {
	  lastCharacter = null
	} else {
	  val localCharacter = lastCharacter
	  val character = deck.characters[actor]
	  if (localCharacter != character) {
		if (character != null)
		  characterChanged(character, localCharacter)

		lastCharacter = character
	  }
	}
	verticalBox?.visible = lastCharacter != null
  }
}
