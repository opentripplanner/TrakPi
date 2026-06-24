package org.opentripplanner.trakpi.otp

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.Kpi
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** Fewest transfers across the returned itineraries (transit legs minus one); 0 when none. */
class MinTransfersKPICalculator : KPICalculator {
    override fun calculate(response: TravelPlannerResponse): Kpi {
        val transfers =
            response.tripPatterns().map { tp ->
                val legs = tp["legs"] as? JsonArray ?: JsonArray(emptyList())
                val transitLegs = legs.count { it.jsonObject["mode"]?.jsonPrimitive?.content != "foot" }
                (transitLegs - 1).coerceAtLeast(0)
            }.minOrNull() ?: 0
        return Kpi("minTransfers", transfers.toDouble())
    }
}
