package org.opentripplanner.trakpi.otp

import java.nio.file.Path
import org.opentripplanner.trakpi.runTrakpi
import org.opentripplanner.trakpi.storage.file.FileResultsStorage

private const val OTP_DEV_ENDPOINT = "https://api.dev.entur.io/journey-planner/v3/graphql"

// Wires the OTP implementations of the trakpi SPI to the CLI. Results are written to the directory
// named by TRAKPI_RESULTS_DIR (default "results"); CI uploads that directory as an artifact.
fun main(args: Array<String>) =
    runTrakpi(
        args,
        requestLoader = OtpRequestLoader(),
        travelPlanner = OTPTravelPlanner(OTP_DEV_ENDPOINT, clientName = "entur-trakpi-dev"),
        kpiCalculators = listOf(ItineraryCountKPICalculator()),
        resultsStorage = FileResultsStorage(Path.of(System.getenv("TRAKPI_RESULTS_DIR") ?: "results")),
    )
