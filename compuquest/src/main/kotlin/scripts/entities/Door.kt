package scripts.entities

import compuquest.simulation.combat.DamageTarget
import compuquest.simulation.combat.Damages
import compuquest.simulation.general.*
import compuquest.simulation.intellect.navigation.CrowdAgentNode
import compuquest.simulation.intellect.navigation.agentHeight
import compuquest.simulation.intellect.navigation.agentRadius
import compuquest.simulation.intellect.navigation.newCrowdAgentParams
import godot.Spatial
import godot.annotation.RegisterClass
import org.recast4j.detour.crowd.CrowdAgentParams
import silentorb.mythic.ent.Id

@RegisterClass
class Door : Spatial(), DamageTarget, Interactive, EntityNode, CrowdAgentNode {
	override fun onDamage(world: World, damages: Damages) {
	}

	override fun getInteractable(world: World): Interactable? =
		null

	override fun onInteraction(world: World, actor: Id) {
	}

	override fun getCrowdAgentParams(): CrowdAgentParams =
		newCrowdAgentParams(
			maxSpeed = 0f,
			radius = agentRadius * 4,
			height = agentHeight * 2,
		)
}
