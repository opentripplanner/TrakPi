package org.opentripplanner.trakpi.otp

import org.opentripplanner.trakpi.tester.spi.ResultsStorage
import org.opentripplanner.trakpi.tester.spi.TestCaseResult

/** Writes results to standard output. */
class StdOutResultsStorage : ResultsStorage {
    override fun store(result: TestCaseResult) {
        println("Result for ${result.requestId}:")
        for (kpi in result.kpis) {
            println("  ${kpi.name} = ${kpi.value}")
        }
    }
}
