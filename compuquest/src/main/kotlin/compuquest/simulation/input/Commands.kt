package compuquest.simulation.input

import compuquest.population.nextLevelEvent

object Commands {
  val interact = "interact"
  val finishInteraction = "finishInteraction"
  val menuBack = "back"
  val activate = "activate"
//  val manageMembers = "manageMembers"
//  val manageQuests = "manageQuests"
  val newGame = "newGame"
  val addPlayer = "addPlayer"
  val removePlayer = "removePlayer"
  val navigate = "navigate"
  val drillDown = "drillDown"
  val primaryAction = "primaryAction"
  val jump = "jump"
  val secondaryAction = "secondaryAction"
  val nextAction = "nextAction"
  val previousAction = "previousAction"

  const val lookLeft = "lookLeft"
  const val lookRight = "lookRight"
  const val lookUp = "lookUp"
  const val lookDown = "lookDown"

  const val moveForward = "moveForward"
  const val moveBackward = "moveBackwards"
  const val moveLeft = "moveLeft"
  const val moveRight = "moveRight"
  const val moveUp = "moveUp"
  const val moveDown = "moveDown"

  const val moveLengthwise = "moveHorizontal"
  const val moveLateral = "moveVertical"
  const val lookX = "lookHorizontal"
  const val lookY = "lookVertical"

  // Debug Commands

  // Only used for flying down.
  // Using the name "crouch" just in case crouching is ever added to the game (maybe even as a third-party mod).
  // Crouching is not a planned feature for this game.
  val crouch = "crouch"

  val nextLevel = nextLevelEvent
}
