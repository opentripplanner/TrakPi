package org.opentripplanner.trakpi.model

import org.opentripplanner.trakpi.model.TransitMode.Companion.DEFAULT
import org.opentripplanner.trakpi.model.TransitMode.Companion.TRANSIT
import java.time.Duration
import java.time.LocalDateTime


/**
 * We do not support all modes: CABLE_CAR, GONDOLA, FUNICULAR is not supported, add modes if needed.
 */
enum class TransitMode {
    WALK, BIKE, CAR, BUS, SUBWAY, TRAM, TRAIN, FERRY, AIRPLANE;

    companion object {
        val TRANSIT = setOf(BUS, SUBWAY, TRAM, TRAIN, FERRY, AIRPLANE)
        val DEFAULT = TRANSIT.plus(WALK)
    }
}

fun toString(modes : Set<TransitMode>) : String {
    return when(modes) {
        DEFAULT -> "DEFAULT"
        TRANSIT -> "TRANSIT"
        else -> modes.toString()
    }
}


data class Operator(
        val id: String,
        val name: String
) {
    override fun toString() = name
}

val defaultOperator = Operator("unkown", "Unknown")

data class Route(
        val id: String,
        val name: String,
        val operator: Operator = defaultOperator
) {
    override fun toString() = name
}


fun location(name: String, coordinate: Coordinate): Place {
    return Place(name, null, coordinate)
}

fun stopPlace(name: String, stopId : String, coordinate: Coordinate?): Place {
    return Place(name, stopId, coordinate)
}

data class Place(
    val name: String,
    val stopId: String?,
    val coordinate: Coordinate?
) {
    init {
        if(stopId == null && coordinate == null) {
            error("At least one of 'stopId' and 'coordinate' is required. $this")
        }
    }

    override fun toString() : String {
        var text = name
        if(stopId != null) text += " #$stopId"
        if(coordinate != null) text += " ${coordinate}"
        return text
    }
}

data class Leg(
        val mode: TransitMode,
        val departureTime: LocalDateTime,
        val arrivalTime: LocalDateTime,
        val route: Route?,
        val distance: Distance?
)

data class Journey(
        val places: List<Place>,
        val legs: List<Leg>,
        val waitingTime: Duration,
        val routingCost: Int
) {
    val departureTime: LocalDateTime = legs.first().departureTime
    val arrivalTime: LocalDateTime = legs.last().arrivalTime
    val numberOfTransfers = legs.size - 1
    val duration = Duration.between(departureTime, arrivalTime)
    val walkingDistance = distance(TransitMode.WALK)

    private fun distance(mode: TransitMode): Distance = Distance(
            legsBy(mode).filter { it.distance != null }.map { it.distance!!.mm }.sum()
    )

    private fun legsBy(mode: TransitMode): List<Leg> = legs.filter { it.mode == mode }
}

data class Itinerary(val journeys: List<Journey>)
