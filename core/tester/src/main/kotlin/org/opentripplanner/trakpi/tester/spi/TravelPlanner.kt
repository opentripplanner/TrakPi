package org.opentripplanner.trakpi.tester.spi

/** Executes a request against a travel planner. */
interface TravelPlanner<in R : TravelPlannerRequest> {
    fun execute(request: R): TravelPlannerResponse
}
