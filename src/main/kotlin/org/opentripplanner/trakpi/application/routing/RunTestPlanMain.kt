package org.opentripplanner.trakpi.application.routing

import org.opentripplanner.trakpi.application.DomainService
import org.opentripplanner.trakpi.application.routing.clients.ClientFactory
import org.opentripplanner.trakpi.model.PlannerAPI
import org.opentripplanner.trakpi.model.TestRun
import java.time.LocalDateTime

fun main(vararg args : String) {
    println("TODO: Run a test plan and save results: ${args.joinToString()}")
    RunTestPlanMain("TODO", "TODO", "TODO", "TODO").run()
}

class RunTestPlanMain(
    val planName : String,
    val profileName : String,
    val apiName : String,
    val plannerName : String
) {


    fun run() {
        val plan = DomainService.findTestPlan(planName)
        val profile = DomainService.findTestProfile(profileName)
        val api =  PlannerAPI.find(apiName)
        val planner = DomainService.findPlanner(plannerName)


        val client = ClientFactory.create(api, planner, profile)

        val startTime = LocalDateTime.now()
        val result = client.route(plan.testCases)
        val endTime = LocalDateTime.now()

        val testRun = TestRun(
            description = "TODO: Generate a good description",
            testPlan = plan.name,
            planner = planner.name,
            testStarted = startTime,
            testEnded = endTime,
            plannerAPI = api,
            profile = profile,
            tags = emptyList(), // TODO add support for adding tags to a test run - generate?
            result = result
        )
        DomainService.saveTestRun(testRun)
    }
}