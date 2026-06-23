package org.opentripplanner.trakpi.config

import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.bufferedReader

/**
 * Resolves a [TrakpiConfig] from an optional properties file overlaid with command-line overrides.
 * Override values take precedence over the file; values are trimmed and blank ones ignored.
 */
object TrakpiConfigLoader {
    fun load(configFile: Path?, overrides: Map<String, String>): TrakpiConfig {
        val values = LinkedHashMap<String, String>()
        if (configFile != null) {
            val properties = Properties()
            configFile.bufferedReader(Charsets.UTF_8).use(properties::load)
            for ((key, value) in properties) {
                put(values, key.toString(), value.toString())
            }
        }
        for ((key, value) in overrides) {
            put(values, key, value)
        }
        return TrakpiConfig.from(values)
    }

    private fun put(values: MutableMap<String, String>, key: String, value: String) {
        val trimmed = value.trim()
        if (trimmed.isNotEmpty()) values[key] = trimmed
    }
}
