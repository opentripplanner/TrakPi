package org.opentripplanner.trakpi.model


enum class SearchMode { DEPART_FROM, ARRIVE_BY, TIME_TABLE }

data class TravelRequest(
    /** The location/place where the journey starts */
    val origin: Place,
    /** The location/place where the journey ends */
    val destination: Place,
    /** The earliest time the journey can depart from the origin. */
    val fromTime: TimeOfDay?,
    /** The latest time the journey can arrive at the destination. */
    val toTime: TimeOfDay?,
    /** List of whitelisted travel modes. Default is WALK + all transit modes. */
    val transitModes: Set<TransitMode> = TransitMode.DEFAULT,
    /** Search mode, default is DEPART_FROM */
    val searchMode: SearchMode = SearchMode.DEPART_FROM
) {
    override fun toString(): String {
        return "TravelRequest(origin=$origin, destination=$destination, fromTime=$fromTime, toTime=$toTime, " +
               "transitModes=${toString(transitModes)}, searchMode=$searchMode)"
    }
}

class TestCase(
        id : String? = null,
        name: String,
        val request: TravelRequest,
        val tags: Set<Tag> = emptySet()
) : NamedEntity(id, name) {
    override fun toString() = "TestCase(id=$_id, name=$name, request=$request, tags=$tags)"
}

/**
 * A _TestPlan_ is a _named_ collection of _TestCases_ defined by the tag expression.
 * The set of test cases is matched when a test run is performed, not before.
 */
class TestPlan(
    id: String? = null,
    name: String,
    val ucTagsExpr: TagExpression,
    val testCases: List<TestCase>
) : NamedEntity(id, name) {
    override fun toString() = "TestPlan(id=$_id, name=$name, ucTagsExpr=$ucTagsExpr, " +
                              "testCases=${testCases.map{ it.name }})"
}
