package silentorb.mythic.ent

typealias Id = Long

typealias NextId = () -> Id

data class SharedNextId(
  var value: Id = 1L,
) {
  fun source(): NextId = { value++ }
}

fun newIdSource(initialValue: Id = 1L): NextId {
  var nextId: Id = initialValue
  return { nextId++ }
}

fun <K, V, O> mapEntry(transform: (K, V) -> O): (Map.Entry<K, V>) -> O = { entry ->
  transform(entry.key, entry.value)
}

fun <K, V, O> mapEntryValue(transform: (V) -> O): (Map.Entry<K, V>) -> O = { entry ->
  transform(entry.value)
}
