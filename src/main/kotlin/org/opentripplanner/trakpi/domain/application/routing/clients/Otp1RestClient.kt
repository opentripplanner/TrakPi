package org.opentripplanner.trakpi.domain.application.routing.clients

import org.opentripplanner.trakpi.domain.model.Planner
import org.opentripplanner.trakpi.domain.model.TestCase
import org.opentripplanner.trakpi.domain.model.TestProfile
import org.opentripplanner.trakpi.domain.model.TestResult

class Otp1RestClient(val planner: Planner, val profile: TestProfile) : RoutingClient {

    override fun route(testCases: List<TestCase>): List<TestResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}