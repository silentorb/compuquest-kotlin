package compuquest.simulation.intellect.navigation

import org.recast4j.detour.crowd.CrowdAgent
import org.recast4j.detour.crowd.PathQueryResult
import java.lang.reflect.Field

// Would rather initialize these in-place but for some reason that causes them to be for this file's class
// instead of the calling class.
var crowdAgent_targetPathQueryResult: Field? = null
var pathQueryResult_status: Field? = null

private var initialized = false

fun initNavigationHacks() {
	if (initialized)
		return

	initialized = true
	crowdAgent_targetPathQueryResult = CrowdAgent(0).javaClass.getDeclaredField("targetPathQueryResult")
	crowdAgent_targetPathQueryResult!!.trySetAccessible()
	pathQueryResult_status = PathQueryResult().javaClass.getDeclaredField("status")
	pathQueryResult_status!!.trySetAccessible()
}
