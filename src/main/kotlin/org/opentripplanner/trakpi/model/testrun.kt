package org.opentripplanner.trakpi.model

import java.time.Duration
import java.time.LocalDateTime

data class TestResult(
        val testCaseId: String,
        val success: Boolean,
        val result: Itinerary?,
        val responseTime: Duration
) {
    override fun toString(): String = "Result{tc: $testCaseId, success: $success, " +
            "responseTime: ${durationToString(responseTime)}, result: $result }"
}

/**
 * A test profile is a named set of extra search parameters/configuration to be able
 * to run the same test set for different APIs, planners and so on. The mapping
 * to the API parameter must be done in the API adapter. The adapter may choose
 * to ignore parameters, if so they should be logged.
 */
class TestProfile(
        id: String? = null,
        name: String,
        val description: String,
        val walkReluctance : Double? = null,
        val walkSpeed : Double? = null,
        val bikeSpeed : Double? = null,
        val wheelchairsAccessibly : Boolean? = null
) : NamedEntity(id, name)

enum class PlannerAPI(val alias : String,  val description : String) {
    OTP2_TRANSMODEL("Netex", "OTP v2.x - Transmodel/Netex API (GraphQL)"),
    OTP2_GTFS("GTFS", "OTP v1.x - GTFS API (GraphQL)"),
    OTP1_REST("REST","OTP v1.x - GTFS API (REST)");

    companion object {
        fun find(apiName: String): PlannerAPI {
            for (it in values()) {
                if (it.name.equals(apiName, true) || it.alias.equals(apiName, true)) {
                    return it
                }
            }
            error("Unable to find '$apiName' in ${values().joinToString()}")
        }
    }
}

class Planner(
        id : String? = null,
        name: String,
        val baseUrl: String,
        val description: String = "<Additional information like OS, Architecture and Planner config>"
) : NamedEntity(id, name) {
    override fun toString(): String {
        return "Planner(id=$_id, name=$name, baseUrl='$baseUrl', description='$description')"
    }
}

class TestRun(
    _id: String? = null,
    val description: String,
    val testPlan: String,
    val planner: String,
    val plannerAPI: PlannerAPI,
    val testStarted: LocalDateTime,
    val testEnded: LocalDateTime,
    val profile: TestProfile,
    val tags: List<Tag>,
    val result: List<TestResult>
) : Entity(_id)
