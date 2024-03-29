package silentorb.mythic.ent

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

data class DeckReflection<Deck : Any, Hand : Any>(
  val deckType: KClass<Deck>,
  val deckConstructor: KFunction<Deck>,
  val deckProperties: List<KProperty1<Deck, *>>,
  val handType: KClass<Hand>,
  val handProperties: List<KProperty1<Hand, *>?>,
  val blacklist: List<Any>,
)

fun <Deck : Any, Hand : Any> newDeckReflection(
  deckType: KClass<Deck>,
  handType: KClass<Hand>,
  blacklist: List<Pair<String, Any>> = listOf()
): DeckReflection<Deck, Hand> {
  val deckConstructor = deckType.constructors.first()
  val blacklistKeys = blacklist.map { it.first }
  val parameters = deckConstructor.parameters
    .filter { p -> !blacklistKeys.contains(p.name) }

  val deckProperties = parameters
    .map { p -> deckType.memberProperties.first { it.name == p.name } }

  val handProperties = parameters
    .map { p ->
      handType.memberProperties.firstOrNull {
        it.returnType.jvmErasure.jvmName == p.type.arguments[1].type!!.jvmErasure.jvmName
      }
    }

  return DeckReflection(
    deckType = deckType,
    deckConstructor = deckConstructor,
    deckProperties = deckProperties,
    handType = handType,
    handProperties = handProperties,
    blacklist = blacklist.map { it.second }
  )
}

fun <Deck : Any, Hand : Any> newReflectedDeck(
  deckReflection: DeckReflection<Deck, Hand>,
  args: List<Any> = listOf()
): Deck =
  deckReflection.deckConstructor.call(*args.plus(deckReflection.blacklist).toTypedArray())

fun <Deck : Any, Hand : Any> genericRemoveEntities(deckReflection: DeckReflection<Deck, Hand>): (Collection<Any>) -> (Deck) -> Deck =
  { removeIds ->
    { deck ->
      val isActive = { id: Any -> !removeIds.contains(id) }
      val deletions = deckReflection.deckProperties.map { property ->
        val value = property.get(deck) as Map<Any, Any>
        value.filterKeys(isActive)
      }
      newReflectedDeck(deckReflection, deletions)
    }
  }

fun <Deck : Any, Hand : Any> genericMergeDecks(deckReflection: DeckReflection<Deck, Hand>): (Deck, Deck) -> Deck =
  { first, second ->
    val additions = deckReflection.deckProperties.map { property ->
      val a = property.get(first) as Table<Any>
      val b = property.get(second) as Table<Any>
      a.plus(b)
    }
    newReflectedDeck(deckReflection, additions)
  }
