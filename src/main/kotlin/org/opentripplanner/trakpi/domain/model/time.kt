package org.opentripplanner.trakpi.domain.model

import java.time.LocalTime


data class TimeOfDay(val hh: Int, val mm: Int, val ss: Int = 0, val offsetDays: Int = 0) {
    override fun toString(): String {
        return if (ss == 0 && offsetDays == 0) String.format("%02d:%02d", hh, mm)
        else if (ss == 0)                      String.format("%02d:%02d+%d", hh, mm, offsetDays)
        else if (offsetDays == 0)              String.format("%02d:%02d:%02d", hh, mm, ss)
        else                                   String.format("%02d:%02d:%02d+%d", hh, mm, ss, offsetDays)
    }
}

fun parseTimeOfDay(text: String): TimeOfDay {
    var timePart = text
    var offset = "0"

    val posPlus = text.indexOf('+')

    if (posPlus >= 0) {
        timePart = text.substring(0, posPlus)
        offset = text.substring(posPlus + 1)
    }
    val t = LocalTime.parse(timePart)
    return TimeOfDay(t.hour, t.minute, t.second, offset.toInt())
}


private class DurationStringBuilder(var rest: Int) {
    var buf = ""

    fun add(modulus: Int, suffix: String) {
        if (rest == 0) return

        val value = rest % modulus
        rest /= modulus

        prepend(value, suffix)
    }

    fun addRest(suffix: String) {
        prepend(rest, suffix)
    }

    private fun prepend(value : Int, suffix: String) {
        if (value == 0) return
        buf = value.toString() + suffix + buf
    }

    override fun toString(): String {
        return buf
    }
}

/**
 * Convert number of seconds to a compact human readable strings like:
 *
 * - Positive values: `1d5h3m40s, 2d5m3, 2h5m, 2m5s, 2d, 5h, 40m, 30s
 * - Zero: 0s
 * - Negative duration: -3d5h45s
 *
 * - Zero ´0´ patial values are skipped
 */
fun durationToString(seconds: Int): String {
    if (seconds < 0) return "-" + durationToString(-seconds)
    if (seconds < 60) return "${seconds}s"

    val str = DurationStringBuilder(seconds)
    str.add(60, "s")
    str.add(60, "m")
    str.add(24, "h")
    str.addRest("d")
    return str.toString()
}

fun durationToString(d: java.time.Duration): String = durationToString(d.toSeconds().toInt())
