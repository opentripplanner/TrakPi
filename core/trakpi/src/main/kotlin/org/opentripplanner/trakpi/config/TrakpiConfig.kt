package org.opentripplanner.trakpi.config

import java.nio.file.Path
import kotlin.io.path.isDirectory

/** Resolved trakpi configuration for a run. */
data class TrakpiConfig(val requestsDir: Path) {
    companion object {
        /** Reads and validates the settings from merged key/value pairs. */
        fun from(values: Map<String, String>): TrakpiConfig {
            val requestsDir =
                values["requests.dir"]?.let(Path::of)
                    ?: throw IllegalArgumentException("'requests.dir' must be set in the config file or as a command-line override")
            require(requestsDir.isDirectory()) { "requests directory does not exist: $requestsDir" }
            return TrakpiConfig(requestsDir = requestsDir)
        }
    }
}
