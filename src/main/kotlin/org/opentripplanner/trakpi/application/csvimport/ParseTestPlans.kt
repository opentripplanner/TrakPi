package org.opentripplanner.trakpi.application.csvimport

import org.opentripplanner.trakpi.model.TagExpression
import org.opentripplanner.trakpi.model.TestPlan

internal class ParseTestPlans  (val parser: CsvParser) {
    companion object {
        private const val COL_NAME = "name"
        private const val COL_UC_TAGS_EXPRESSION = "ucTagsExpr"
    }

    fun parse(): List<TestPlan> {
        parser.readHeader(setOf(COL_NAME, COL_UC_TAGS_EXPRESSION))

        val result = arrayListOf<TestPlan>()

        while (parser.hasMoreLines()) {
            result += TestPlan(
                name = parser.read(COL_NAME),
                ucTagsExpr = TagExpression(parser.read(COL_UC_TAGS_EXPRESSION)),
                testCases = emptyList()
            )
        }
        parser.parsingComplete()
        return result
    }
}