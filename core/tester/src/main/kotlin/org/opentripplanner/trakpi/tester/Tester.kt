package org.opentripplanner.trakpi.tester

import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.RequestLoader
import org.opentripplanner.trakpi.tester.spi.ResultsStorage
import org.opentripplanner.trakpi.tester.spi.TestCaseResult
import org.opentripplanner.trakpi.tester.spi.TravelPlanner
import org.opentripplanner.trakpi.tester.spi.TravelPlannerRequest

/**
 * Runs a test: loads each request file, executes it against the travel planner, computes the KPIs
 * for the response, and stores the result.
 */
class Tester<R : TravelPlannerRequest>(
    private val requestFileLoader: RequestFileLoader,
    private val requestLoader: RequestLoader<R>,
    private val travelPlanner: TravelPlanner<R>,
    private val kpiCalculators: List<KPICalculator>,
    private val resultsStorage: ResultsStorage,
) {
    fun run() {
        for (file in requestFileLoader.loadAll()) {
            val request = requestLoader.load(file)
            val response = travelPlanner.execute(request)
            val kpis = kpiCalculators.map { it.calculate(response) }
            resultsStorage.store(TestCaseResult(file.id, response.raw, kpis))
        }
    }
}
