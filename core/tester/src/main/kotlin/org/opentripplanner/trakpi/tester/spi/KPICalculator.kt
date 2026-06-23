package org.opentripplanner.trakpi.tester.spi

/** Computes one [Kpi] from a planner response */
interface KPICalculator {
    fun calculate(response: TravelPlannerResponse): Kpi
}
