package compuquest.simulation.combat

typealias DamageType = String

data class DamageDefinition(
    val type: DamageType,
    val amount: Int
)

data class Weapon(
    val attackMethod: AttackMethod,
    val damages: List<DamageDefinition>,
    val damageRadius: Float = 0f,
    val velocity: Float = 1f,
    val damageFalloff: Float = 0f,
//    val sound: SoundType? = null,
    val impulse: Float = 0f,
//    val missileMesh: MeshName? = null,
    val yawOffset: Float = 0f,
)
