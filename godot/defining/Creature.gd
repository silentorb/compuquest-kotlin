extends Resource

export(String) var depiction

export(int) var frame = 0

export(int) var health

export(Array, Resource) var accessories

export(int) var fee = 0

export(Array, Resource) var quests

export(String) var key

export(Array, String, "forHire", "talk", "quests") var attributes

export(Array, Resource) var wares

export(float) var enemyVisibilityRange = 0
