package silentorb.mythic.debugging

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.nio.file.Paths

private var dotEnv: Dotenv? = null

// Nullable for potentially faster access checks when not being used.
// (Only potentially because the JVM may already perform a similar optimization for empty MutableMaps)
// It's only an internal implementation difference so can be easily changed later if needed.
private var overrides: MutableMap<String, Any>? = null

private var lastModified: Long = 0L

fun getDotEnvDirectory(): String =
    System.getenv("DOTENV_DIRECTORY") ?: ""

fun newDotEnv() = dotenv {
  directory = getDotEnvDirectory()
  ignoreIfMissing = true
}

fun reloadDotEnv() {
  dotEnv = newDotEnv()
}

fun checkDotEnvChanged() {
  val dotEnvDirectory = getDotEnvDirectory()
  val modified = File(Paths.get(dotEnvDirectory, ".env").toUri()).lastModified()
  if (modified > lastModified) {
    lastModified = modified
    reloadDotEnv()
    println("Detected .env changes and reloaded ${java.util.Date()}")
  }
}

fun getDebugString(name: String): String? {
  val localOverrides = overrides
  if (localOverrides != null) {
    val value = localOverrides[name]
    if (value != null)
      return value.toString()
  }
  dotEnv = dotEnv
      ?: newDotEnv()
  return dotEnv!![name]
}

fun setDebugValue(name: String, value: Any) {
  if (overrides == null)
    overrides = mutableMapOf()

  overrides!![name] = value
}

fun toggleDebugBoolean(name: String) {
  val value = getDebugBoolean(name)
  setDebugValue(name, if (value) 0 else 1)
}

fun getConfigString(name: String): String? =
    getDebugString(name)

fun getDebugInt(name: String): Int? =
    getDebugString(name)?.toIntOrNull()

fun getDebugLong(name: String): Long? =
    getDebugString(name)?.toLongOrNull()

fun getDebugBoolean(name: String): Boolean =
    getDebugInt(name)?.equals(1) ?: false

fun getDebugFloat(name: String): Float? =
    getDebugString(name)?.toFloatOrNull()

fun conditionalDebugLog(booleanSetting: String): (() -> String) -> Unit = { message ->
  if (getDebugBoolean(booleanSetting)) {
    println(message())
  }
}

fun getDebugOverrides() =
    overrides?.toMap() ?: mapOf()
