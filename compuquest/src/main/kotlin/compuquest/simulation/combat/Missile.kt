package compuquest.simulation.combat

data class Missile(
  val damageRadius: Float, // 0f for no AOE
  val damageFalloff: Float, // Falloff Exponent
  val damages: List<DamageDefinition>
)
