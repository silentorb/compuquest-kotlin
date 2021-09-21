extends Resource

export(String, "attack", "heal") var effect
export(float) var strength
export(float) var Range
export(String, "mana", "money") var costResource
export(int) var costAmount
export(float) var cooldown
export(Array, String) var attributes
export(Resource) var spawns
#export(Array, Resource) var effects
