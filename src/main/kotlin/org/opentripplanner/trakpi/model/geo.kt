package org.opentripplanner.trakpi.model

import kotlin.math.roundToInt


data class Distance(val mm : Int) {
    override fun toString() : String {
        return if(mm < 0) "-" + mmToMetersString(-mm) else mmToMetersString(mm)
    }
    companion object {
        fun meters(m: Int): Distance {
            return Distance(1000 * m)
        }
    }
}

data class Coordinate(val lat:Double, val lon:Double) {
    override fun toString() = "($lat, $lon)"
}


/**
 * Pretty print a distance(mm) with
 *  - above 10 metres with metre precision
 *  - below 10 metres with deci-metre precision
 *  - below 1 metre with centi-metre precision
 */
private fun mmToMetersString(mm : Int) : String {
    val mmRest = mm % 1000
    val metre = mm / 1000

    if(mmRest == 0) return "${metre}m"

    if(metre >= 10) {
        return if(mmRest >= 500) "${metre + 1}m" else "${metre}m"
    }

    if(metre >= 1) {
        val dmRest = (mmRest / 100.0f).roundToInt()
        if(dmRest == 10) return (metre+1).toString() + ".0m"
        else return "${metre}.${dmRest}m"
    }

    val cmRest = (mmRest / 10.0f).roundToInt()
    if(cmRest == 100) return (metre+1).toString() + ".00m"
    else return String.format("%d.%02dm", metre, cmRest)
}
