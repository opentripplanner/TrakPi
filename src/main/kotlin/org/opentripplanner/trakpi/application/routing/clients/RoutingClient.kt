package org.opentripplanner.trakpi.application.routing.clients

import org.opentripplanner.trakpi.model.TestCase
import org.opentripplanner.trakpi.model.TestResult

interface RoutingClient {
    fun route(testCases : List<TestCase>) : List<TestResult>
}