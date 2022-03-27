package compuquest.definition

import compuquest.simulation.characters.AccessoryEffect
import compuquest.simulation.characters.AccessoryEffects
import compuquest.simulation.characters.AccessoryIntervals
import compuquest.simulation.characters.EffectRecipient
import godot.core.Transform
import godot.core.Vector3

val summonOffset = Transform().translated(Vector3(0, 0, -1.6))

val removeOnUseAny = AccessoryEffect(
	type = AccessoryEffects.removeOnUseAny,
	interval = AccessoryIntervals.continuous,
	recipient = EffectRecipient.self,
)
