package org.opentripplanner.trakpi.application

import org.opentripplanner.trakpi.model.Planner
import org.opentripplanner.trakpi.model.TestCase
import org.opentripplanner.trakpi.model.TestPlan
import org.opentripplanner.trakpi.model.TestProfile
import org.opentripplanner.trakpi.model.TestRun
import org.opentripplanner.trakpi.store.framework.DbClient

object DomainService {
    private val defaultTestProfile = TestProfile(
        name="Default",
        description = "Default test profile, no params set. Use API defaults."
    )

    fun listTestCases() : List<TestCase> {
        return DbClient.getTestCases().map { it.toDomainObj() }
    }

    fun listTestPlans() : List<TestPlan> {
        val tcList = listTestCases()
        return DbClient.getTestPlans().map { it.toDomainObj(tcList) }
    }

    fun findTestPlan(name : String) : TestPlan {
        return listTestPlans().first { it.name.equals(name, true)}
    }

    fun findTestProfile(profileName : String): TestProfile {
        // TODO Add support for more than default profile
        return defaultTestProfile
    }

    fun findPlanner(plannerName: String): Planner {
        // TODO Add some error handling on missing planner
        return DbClient.getPlanners().first { it.name.equals(plannerName, true) }.toDomainObj()
    }

    fun saveTestRun(testRun: TestRun) {
        TODO("not implemented")
    }
}
