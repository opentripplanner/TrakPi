package org.opentripplanner.trakpi.otp

import org.opentripplanner.trakpi.runTrakpi

// TODO: once the tester SPI lands (RequestLoader, TravelPlanner, KPICalculator, ResultsStorage),
//  construct the OTP implementations here and pass them to runTrakpi.
fun main(args: Array<String>) = runTrakpi(args)
