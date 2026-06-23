package org.opentripplanner.trakpi.tester

import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import org.opentripplanner.trakpi.tester.spi.RequestFile

/** Reads request files from [dir], in filename order. The id of each is its filename without extension. */
class RequestFileLoader(private val dir: Path) {
    fun loadAll(): List<RequestFile> =
        dir.listDirectoryEntries()
            .filter { it.isRegularFile() }
            .sorted()
            .map { RequestFile(id = it.nameWithoutExtension, body = it.readText()) }
}
