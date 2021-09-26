package scripts.gui

enum class ManagementScreens {
  members,
  quests,
}

fun stringToManagementScreen(value: String): ManagementScreens? =
  ManagementScreens.values().firstOrNull { it.name == value }
