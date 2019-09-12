package org.opentripplanner.trakpi.store.schema

import org.opentripplanner.trakpi.model.Planner
import org.opentripplanner.trakpi.model.PlannerAPI
import org.opentripplanner.trakpi.model.TestProfile
import org.opentripplanner.trakpi.model.TestResult
import java.time.LocalDateTime


class DbTestProfile(
    id: String? = null,
    name: String,
    val description: String,
    val walkReluctance : Double? = null,
    val walkSpeed : Double? = null,
    val bikeSpeed : Double? = null,
    val wheelchairsAccessibly : Boolean? = null
) : DbNamedEntity(id, name)

class DbPlanner(
    id : String? = null,
    name: String,
    val description: String,
    val baseUrl: String
) : DbNamedEntity(id, name) {
    constructor(p : Planner) : this(p._id, p.name, p.baseUrl, p.description)
    override fun toString(): String {
        return "Planner(id=$_id, name=$name, baseUrl='$baseUrl', description='$description')"
    }

    fun toDomainObj() : Planner {
        return Planner(_id, name, baseUrl, description)
    }
}

class DbTestRun(
    _id: String? = null,
    val description: String,
    val testPlan: String,
    val planner: String,
    val plannerAPI: PlannerAPI,
    val testStarted: LocalDateTime,
    val testEnded: LocalDateTime,
    val profile: TestProfile,
    val tags: List<String> = arrayListOf(),
    val result: List<TestResult>
) : DbEntity(_id) {
    override fun toString(): String {
        return "DbTestRun(description='$description', testPlan='$testPlan', planner='$planner', " +
               "plannerAPI=$plannerAPI, testStarted=$testStarted, testEnded=$testEnded, profile=${profile.name}, " +
               "tags=$tags, result=$result)"
    }
}
