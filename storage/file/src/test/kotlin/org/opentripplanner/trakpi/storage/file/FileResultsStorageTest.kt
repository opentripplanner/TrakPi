package org.opentripplanner.trakpi.storage.file

import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.opentripplanner.trakpi.tester.spi.Kpi
import org.opentripplanner.trakpi.tester.spi.TestCaseResult

class FileResultsStorageTest {
    @Test
    fun `writes a json file per result`() {
        val dir = Files.createTempDirectory("results")
        val clock = Clock.fixed(Instant.parse("2026-06-23T04:00:00Z"), ZoneOffset.UTC)
        val storage = FileResultsStorage(dir, clock)

        storage.store(
            TestCaseResult(
                requestId = "request-1",
                rawResponse = """{"data":{"trip":{"tripPatterns":[]}}}""",
                kpis = listOf(Kpi("itineraryCount", 5.0)),
            )
        )

        val file = dir.resolve("request-1.json")
        assertTrue(file.exists())
        val obj = Json.parseToJsonElement(file.readText()).jsonObject
        assertEquals("request-1", obj["requestId"]!!.jsonPrimitive.content)
        assertEquals("2026-06-23T04:00:00Z", obj["timestamp"]!!.jsonPrimitive.content)
        assertEquals(5.0, obj["kpis"]!!.jsonObject["itineraryCount"]!!.jsonPrimitive.double)
        assertEquals(
            """{"data":{"trip":{"tripPatterns":[]}}}""",
            obj["rawResponse"]!!.jsonPrimitive.content,
        )
    }
}
