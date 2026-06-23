package org.opentripplanner.trakpi.config

import java.nio.file.Files
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TrakpiConfigLoaderTest {
    @Test
    fun `reads requests dir from the config file`() {
        val requests = Files.createTempDirectory("requests")
        val file = Files.createTempFile("trakpi", ".properties")
        file.writeText("requests.dir=$requests")

        val config = TrakpiConfigLoader.load(configFile = file, overrides = emptyMap())

        assertEquals(requests, config.requestsDir)
    }

    @Test
    fun `reads requests dir from overrides without a config file`() {
        val requests = Files.createTempDirectory("requests")

        val config = TrakpiConfigLoader.load(configFile = null, overrides = mapOf("requests.dir" to "$requests"))

        assertEquals(requests, config.requestsDir)
    }

    @Test
    fun `overrides take precedence over the config file`() {
        val fromFile = Files.createTempDirectory("from-file")
        val fromOverride = Files.createTempDirectory("from-override")
        val file = Files.createTempFile("trakpi", ".properties")
        file.writeText("requests.dir=$fromFile")

        val config = TrakpiConfigLoader.load(configFile = file, overrides = mapOf("requests.dir" to "$fromOverride"))

        assertEquals(fromOverride, config.requestsDir)
    }

    @Test
    fun `trims override values`() {
        val requests = Files.createTempDirectory("requests")

        val config = TrakpiConfigLoader.load(configFile = null, overrides = mapOf("requests.dir" to "  $requests  "))

        assertEquals(requests, config.requestsDir)
    }

    @Test
    fun `blank override is ignored and the file value is kept`() {
        val requests = Files.createTempDirectory("requests")
        val file = Files.createTempFile("trakpi", ".properties")
        file.writeText("requests.dir=$requests")

        val config = TrakpiConfigLoader.load(configFile = file, overrides = mapOf("requests.dir" to "  "))

        assertEquals(requests, config.requestsDir)
    }

    @Test
    fun `fails when requests dir is set neither in overrides nor in a file`() {
        assertFailsWith<IllegalArgumentException> {
            TrakpiConfigLoader.load(configFile = null, overrides = emptyMap())
        }
    }

    @Test
    fun `fails when the config file lacks requests dir`() {
        val file = Files.createTempFile("trakpi", ".properties")
        file.writeText("other.key=value")

        assertFailsWith<IllegalArgumentException> {
            TrakpiConfigLoader.load(configFile = file, overrides = emptyMap())
        }
    }

    @Test
    fun `fails when an override requests dir does not exist`() {
        val missing = Files.createTempDirectory("x").resolve("missing")

        assertFailsWith<IllegalArgumentException> {
            TrakpiConfigLoader.load(configFile = null, overrides = mapOf("requests.dir" to "$missing"))
        }
    }

    @Test
    fun `fails when a file-configured requests dir does not exist`() {
        val missing = Files.createTempDirectory("x").resolve("missing")
        val file = Files.createTempFile("trakpi", ".properties")
        file.writeText("requests.dir=$missing")

        assertFailsWith<IllegalArgumentException> {
            TrakpiConfigLoader.load(configFile = file, overrides = emptyMap())
        }
    }
}
