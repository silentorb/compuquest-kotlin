package compuquest.simulation.general

import compuquest.simulation.updating.simulationFps

const val highIntScale = 1_000_000

typealias HighInt = Int
typealias Int100 = Int // An integer where 100 units = 1f
typealias Int1000 = Int // An integer where 100 units = 1f

const val intMinute = simulationFps * 60
const val intHour = intMinute * 60

//fun highPercentage(value: HighInt): Int =
//    value * 100 / highIntScale
//
//fun percentageToHighInt(value: Int): HighInt =
//    value * highIntScale / 100

//fun toInt100(value: Float): Int100 =
//    (value * 100f).toInt()
//
fun toInt1000(value: Float): Int1000 =
    (value * 1000f).toInt()
