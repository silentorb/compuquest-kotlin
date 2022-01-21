package silentorb.mythic.haft

import silentorb.mythic.ent.HashedMap
import silentorb.mythic.ent.Key

typealias ContextBindings = Map<Key, Bindings>

fun bindingsUseDevice(bindings: ContextBindings, device: Int) =
	bindings.any { (_, contextBindings) ->
		contextBindings.any {
			it.device == device
		}
	}

data class InputProfile(
	val name: String,
	val bindings: ContextBindings,
	val usesKeyboard: Boolean = bindingsUseDevice(bindings, InputDevices.keyboard),
	val usesMouse: Boolean = bindingsUseDevice(bindings, InputDevices.mouse),
	val usesGamepad: Boolean = bindingsUseDevice(bindings, InputDevices.gamepad),
)

data class InputProfileOptions(
	val name: String,
	val bindings: ContextBindings = mapOf(),
	val references: List<Int> = listOf(),
)

typealias InputProfileOptionsMap = Map<Int, InputProfileOptions>
typealias HashedInputProfileOptionsMap = HashedMap<Int, InputProfileOptions>
typealias InputProfileMap = Map<Int, InputProfile>

fun gatherNestedProfiles(profiles: InputProfileOptionsMap, references: List<Int>, used: Set<Int>): Set<Int> =
	references
		.minus(used)
		.filter { profiles.containsKey(it) }
		.fold((references + used).toSet()) { usedAccumulator, reference ->
			val profile = profiles[reference]!!
			gatherNestedProfiles(profiles, profile.references, usedAccumulator)
		}

fun gatherNestedBindings(profiles: InputProfileOptionsMap, references: List<Int>, used: List<Int>): ContextBindings =
	gatherNestedProfiles(profiles, references, used.toSet())
		.fold(mapOf()) { accumulator, reference ->
			val profile = profiles[reference]!!
			(accumulator.keys + profile.bindings.keys)
				.associateWith { context ->
					(accumulator[context] ?: listOf()) + (profile.bindings[context] ?: listOf())
				}
		}

fun compileInputProfiles(profiles: InputProfileOptionsMap): InputProfileMap =
	profiles.mapValues { (id, profile) ->
		InputProfile(
			name = profile.name,
			bindings = gatherNestedBindings(profiles, profile.references, listOf(id)),
		)
	}
