package org.opentripplanner.trakpi.otp

import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.Kpi
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** OTP's server-side routing time from `debugOutput.totalTime` (nanoseconds), reported in milliseconds. */
class RoutingTimeKPICalculator : KPICalculator {
    override fun calculate(response: TravelPlannerResponse): Kpi {
        val nanos = response.tripObject()?.get("debugOutput")?.jsonObject?.get("totalTime")?.jsonPrimitive?.longOrNull ?: 0L
        return Kpi("routingTimeMs", nanos / 1_000_000.0)
    }
}
