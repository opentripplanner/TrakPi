package org.opentripplanner.trakpi.domain.application.csvimport

import org.opentripplanner.trakpi.domain.model.Coordinate
import org.opentripplanner.trakpi.domain.model.Place
import org.opentripplanner.trakpi.domain.model.Tag
import org.opentripplanner.trakpi.domain.model.TestCase
import org.opentripplanner.trakpi.domain.model.TravelRequest
import org.opentripplanner.trakpi.domain.model.location
import org.opentripplanner.trakpi.domain.model.stopPlace


internal class ParseTestCases(val parser: CsvParser) {
    companion object {
        private const val COL_NAME = "name"
        private const val COL_FROM_PREFIX = "from"
        private const val COL_TO_PREFIX = "to"
        private const val COL_PLACE_NAME = "Place"
        private const val COL_PLACE_STOP_ID = "StopId"
        private const val COL_PLACE_COOR_LAT = "Lat"
        private const val COL_PLACE_COOR_LON = "Lon"
        private const val COL_TIME = "Time"
        private const val COL_TAGS = "tags"
        private const val COL_FROM_TIME = COL_FROM_PREFIX + COL_TIME
        private const val COL_TO_TIME = COL_TO_PREFIX + COL_TIME
    }

    fun parse(): List<TestCase> {
        parser.readHeader(
            setOf(COL_NAME, COL_FROM_TIME, COL_TO_TIME, COL_TAGS)
                .plus(placeColumns(COL_FROM_PREFIX))
                .plus(placeColumns(COL_TO_PREFIX))
        )

        val result = arrayListOf<TestCase>()

        while (parser.hasMoreLines()) {
            val request = TravelRequest(
                readPlace(COL_FROM_PREFIX),
                readPlace(COL_TO_PREFIX),
                parser.readTime(COL_FROM_TIME),
                parser.readTime(COL_TO_TIME)
            )
            val dbTestCase = TestCase(
                name = parser.read(COL_NAME),
                request = request,
                tags = Tag.mapStringsToTags(parser.readList (COL_TAGS))
            )
            result += dbTestCase
        }
        logResult(result)
        return result
    }

    private fun logResult(result: List<TestCase>) {
        val tags = hashSetOf<String>()
        result.forEach {
            tags.addAll(Tag.mapTagsToString(it.tags))
        }
        parser.debug("Use case tags in import: $tags")
        parser.parsingComplete()
    }

    private fun readPlace(colPrefix : String): Place {
        val name = parser.read(colPrefix + COL_PLACE_NAME)
        val stopId = parser.readOpt(colPrefix + COL_PLACE_STOP_ID)

        val coordinate = readCoordinate(colPrefix)

        if(stopId == null && coordinate != null) {
            return location(name, coordinate)
        }
        if(stopId != null) {
            return stopPlace(name, stopId, coordinate)
        }
        error(
            "A test-case from/to place must contain a either a stopId or an coordinate: ${parser.rawRecord()}"
        )
    }

    private fun readCoordinate(colPrefix: String): Coordinate? {
        val latitude = parser.readOptDouble(colPrefix + COL_PLACE_COOR_LAT)
        val longitude = parser.readOptDouble(colPrefix + COL_PLACE_COOR_LON)

        return if(latitude == null || longitude == null) null else Coordinate(latitude, longitude)
    }

    private fun placeColumns(colPrefix : String) : Set<String> {
        return setOf(
            colPrefix + COL_PLACE_NAME,
            colPrefix + COL_PLACE_STOP_ID,
            colPrefix + COL_PLACE_COOR_LAT,
            colPrefix + COL_PLACE_COOR_LON
        )
    }
}




