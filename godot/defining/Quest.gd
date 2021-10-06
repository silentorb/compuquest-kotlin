extends Resource

export(String) var name

export(String, "delivery") var type

export(String, MULTILINE) var description

export(String) var recipient

export(int) var rewardGold

export(int) var duration = 0

export(int) var penaltyValue = 0
