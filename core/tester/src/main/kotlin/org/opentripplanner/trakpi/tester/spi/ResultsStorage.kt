package org.opentripplanner.trakpi.tester.spi

/** Persists test results. */
interface ResultsStorage {
    fun store(result: TestCaseResult)
}
