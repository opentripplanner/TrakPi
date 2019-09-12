package org.opentripplanner.trakpi.application.routing.clients

import org.opentripplanner.trakpi.model.Planner
import org.opentripplanner.trakpi.model.TestCase
import org.opentripplanner.trakpi.model.TestProfile
import org.opentripplanner.trakpi.model.TestResult

class Otp1RestClient(val planner: Planner, val profile: TestProfile) : RoutingClient {

    override fun route(testCases: List<TestCase>): List<TestResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}