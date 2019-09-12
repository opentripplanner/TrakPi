package org.opentripplanner.trakpi.domain.application.csvimport

import org.opentripplanner.trakpi.domain.model.Planner


internal class ParsePlanners (val parser: CsvParser) {
    companion object {
        private const val COL_NAME = "name"
        private const val COL_URL = "baseUrl"
        private const val COL_DESCRIPTION = "description"
    }

    fun parse(): List<Planner> {
        parser.readHeader(setOf(COL_NAME, COL_URL, COL_DESCRIPTION))

        val result = arrayListOf<Planner>()

        while (parser.hasMoreLines()) {
            result += Planner(
                name = parser.read(COL_NAME),
                baseUrl = parser.read(COL_URL),
                description = parser.read(COL_DESCRIPTION)
            )
        }
        parser.parsingComplete()
        return result
    }
}
