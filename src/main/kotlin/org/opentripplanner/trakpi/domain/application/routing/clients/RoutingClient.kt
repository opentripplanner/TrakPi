package org.opentripplanner.trakpi.domain.application.routing.clients

import org.opentripplanner.trakpi.domain.model.TestCase
import org.opentripplanner.trakpi.domain.model.TestResult

interface RoutingClient {
    fun route(testCases : List<TestCase>) : List<TestResult>
}