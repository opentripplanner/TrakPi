package org.opentripplanner.trakpi.tester.spi

/** The outcome of running one request. */
data class TestCaseResult(val requestId: String, val rawResponse: String, val kpis: List<Kpi>)
