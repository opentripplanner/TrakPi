package org.opentripplanner.trakpi.otp

import java.nio.file.Path
import org.opentripplanner.trakpi.runTrakpi
import org.opentripplanner.trakpi.tester.RequestFileLoader
import org.opentripplanner.trakpi.tester.Tester

private const val OTP_DEV_ENDPOINT = "https://api.dev.entur.io/journey-planner/v3/graphql"

// Wires the OTP implementations of the trakpi SPI to the CLI.
fun main(args: Array<String>) {
    val requestsDir = Path.of(System.getProperty("trakpi.requestsDir", "reference/otp/requests"))
    val tester =
        Tester(
            requestFileLoader = RequestFileLoader(requestsDir),
            requestLoader = OtpRequestLoader(),
            travelPlanner = OTPTravelPlanner(OTP_DEV_ENDPOINT, clientName = "entur-trakpi-dev"),
            kpiCalculators = listOf(ItineraryCountKPICalculator()),
            resultsStorage = StdOutResultsStorage(),
        )
    runTrakpi(args, tester)
}
