package silentorb.mythic.ent

class HashedMap<K, V>(val value: Map<K, V>) : Map<K, V> by value {
	val hash = value.hashCode()

	override fun hashCode(): Int {
		return hash
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (value === other) return true
		if (javaClass != other?.javaClass) return false

		other as HashedMap<*, *>

		if (hash != other.hash) return false
		if (value.size != other.size) return false
		if (!value.equals(other)) return false

		return true
	}

	companion object {

		// Prevents redundant wrapping
		fun <K, V> from(collection: Map<K, V>) =
			if (collection is HashedMap<K, V>)
				collection
			else
				HashedMap(collection)
	}
}
