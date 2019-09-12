package org.opentripplanner.trakpi.store.schema

import org.opentripplanner.trakpi.domain.model.Tag
import org.opentripplanner.trakpi.domain.model.TagExpression
import org.opentripplanner.trakpi.domain.model.TestCase
import org.opentripplanner.trakpi.domain.model.TestPlan
import org.opentripplanner.trakpi.domain.model.TravelRequest


class DbTestCase(
    id : String? = null,
    name: String,
    val request: TravelRequest,
    val tags: Set<String> = emptySet()
) : DbNamedEntity(id, name) {
    constructor(tc : TestCase) : this(tc._id, tc.name, tc.request, Tag.mapTagsToString(tc.tags))
    fun toDomainObj() = TestCase(_id, name, request, Tag.mapStringsToTags(tags))
    override fun toString() = "DbTestCase(id=$_id, name=$name, tags=$tags, request=$request)"
}

class DbTestPlan(
    id: String? = null,
    name: String,
    val ucTagsExpr: String
) : DbNamedEntity(id, name) {
    constructor(tp : TestPlan) : this(tp._id, tp.name, tp.ucTagsExpr.toString())
    fun toDomainObj(tcList : List<TestCase>) : TestPlan {
        val tcTagExpr = TagExpression(ucTagsExpr)
        val testCases = tcList.filter { tcTagExpr.eval(it.tags) }.toList()
        return TestPlan(_id, name, tcTagExpr, testCases)
    }
    override fun toString() = "DbTestPlan(id=$_id, name=$name, ucTagsExpr=$ucTagsExpr"
}
