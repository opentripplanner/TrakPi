package org.opentripplanner.trakpi.tester.spi

/** A planner's response to a request. The [raw] body is opaque to trakpi. */
data class TravelPlannerResponse(val raw: String)
