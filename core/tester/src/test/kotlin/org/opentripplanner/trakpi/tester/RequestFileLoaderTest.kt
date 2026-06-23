package org.opentripplanner.trakpi.tester

import java.nio.file.Files
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestFileLoaderTest {
    @Test
    fun `id is the filename without extension`() {
        val dir = Files.createTempDirectory("requests")
        dir.resolve("request-2.graphql").writeText("second")
        dir.resolve("request-1.graphql").writeText("first")

        val files = RequestFileLoader(dir).loadAll()

        assertEquals(listOf("request-1", "request-2"), files.map { it.id })
        assertEquals(listOf("first", "second"), files.map { it.body })
    }
}
