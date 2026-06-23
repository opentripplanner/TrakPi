package org.opentripplanner.trakpi.otp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.Kpi
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** Counts the itineraries (trip patterns) in an OTP `trip` response. */
class ItineraryCountKPICalculator : KPICalculator {
    override fun calculate(response: TravelPlannerResponse): Kpi {
        val tripPatterns =
            Json.parseToJsonElement(response.raw)
                .jsonObject["data"]
                ?.jsonObject?.get("trip")
                ?.jsonObject?.get("tripPatterns")
                ?.jsonArray
        return Kpi("itineraryCount", (tripPatterns?.size ?: 0).toDouble())
    }
}
