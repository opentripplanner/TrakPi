package org.opentripplanner.trakpi.otp

import org.opentripplanner.trakpi.runTrakpi

private const val OTP_DEV_ENDPOINT = "https://api.dev.entur.io/journey-planner/v3/graphql"

// Wires the OTP implementations of the trakpi SPI to the CLI.
fun main(args: Array<String>) =
    runTrakpi(
        args,
        requestLoader = OtpRequestLoader(),
        travelPlanner = OTPTravelPlanner(OTP_DEV_ENDPOINT, clientName = "entur-trakpi-dev"),
        kpiCalculators = listOf(ItineraryCountKPICalculator()),
        resultsStorage = StdOutResultsStorage(),
    )
