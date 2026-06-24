package org.opentripplanner.trakpi.otp

import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.Kpi
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** Duration of the fastest itinerary in seconds; 0 when no itineraries were returned. */
class FastestItineraryKPICalculator : KPICalculator {
    override fun calculate(response: TravelPlannerResponse): Kpi {
        val fastest = response.tripPatterns().mapNotNull { it["duration"]?.jsonPrimitive?.longOrNull }.minOrNull() ?: 0L
        return Kpi("fastestDurationSeconds", fastest.toDouble())
    }
}
