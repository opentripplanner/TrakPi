package org.opentripplanner.trakpi.model

import java.time.LocalTime

/**
 * This class represent a Transit service time. A service is relative to the day it operates, and a journey is
 * relative to the day witch the Journey start. Since a service may operate over more than 24 hours and a journey
 * may last for several days this class have an extra "daysOffset".
 * <p/>
 * TimeOfDay can not be negative.
 * <p/>
 * The time should be seen as relative to 12:00 am (noon) minus 12 hours. This is midnight on most days in a year
 * except on days where the time is switching between summer and winter time. This follows the same rules as the
 * GTFS specification. Other than this, we do not make any special attempts to support switching between summer
 * and winter time.
 */
data class TimeOfDay(val hh: Int, val mm: Int, val ss: Int = 0, val daysOffset: Int = 0) {

    /**
     * Format used is 24 hour times like this:
     * - 17:15 (15 minutes past 7 pm)
     * - 17:15:59 (59 seconds and 15 minutes past 7 pm)
     * - 03:15+1d (15 minutes past 3 am plus one extra day)
     * - 23:59:59+3d (1 minite )
     */
    override fun toString(): String {
        return if (ss == 0 && daysOffset == 0) String.format("%02d:%02d", hh, mm)
        else if (ss == 0)                      String.format("%02d:%02d%+dd", hh, mm, daysOffset)
        else if (daysOffset == 0)              String.format("%02d:%02d:%02d", hh, mm, ss)
        else                                   String.format("%02d:%02d:%02d%+dd", hh, mm, ss, daysOffset)
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
