package org.opentripplanner.trakpi.application.csvimport

import com.csvreader.CsvReader
import org.opentripplanner.trakpi.model.TimeOfDay
import org.opentripplanner.trakpi.model.parseTimeOfDay
import org.opentripplanner.trakpi.store.framework.logging.Log
import java.io.File
import java.nio.charset.Charset


/**
 * Reusable CSV file parser. Responsible for:
 * - enforcing required and optional values
 * - parsing simple types like double, LocalTime and LocalTimeWithOffset
 * - error handling; Throwing exceptions on error
 * - debug logging
 */
internal class CsvParser(
    private val csvFile: File
) {
    companion object {
        private val TAG_DELIMITERS_PATTERN = "\\s*[;,]\\s*".toPattern()
    }

    private val log = Log("CsvParser(${csvFile.name})")
    private val reader = CsvReader(csvFile.absolutePath, ',', Charset.forName("UTF-8"))

    init {
        reader.skipEmptyRecords = true
        reader.useComments = true
    }

    fun readHeader(expHeader : Set<String>) {
        info("Parse file: $csvFile")

        val headerRead = reader.readHeaders()

        if (headerRead) {
            debug("Header: " + rawRecord())
            validateHeader(expHeader)
        }
        else {
            error("Unable to parse file. Required header is missing in '$csvFile'.")
        }
    }

    fun hasMoreLines() : Boolean {
        val moreLines = reader.readRecord()
        if(moreLines) debug("Parse line: ${rawRecord()}")
        return moreLines
    }

    fun parsingComplete() {
        debug("Parsing complete!")
    }

    fun readTime(time: String): TimeOfDay? {
        val text = readOpt(time)
        return if(text == null) null else parseTimeOfDay(text)
    }

    fun readList(tags: String): Set<String> {
        return readOpt(tags)?.split(TAG_DELIMITERS_PATTERN)?.toSet() ?: emptySet()
    }

    fun readOptDouble(label: String): Double? = readOpt(label)?.toDouble()

    fun read(column : String) : String {
        val temp = reader[column]
        if(temp.isNullOrEmpty()) error("Required '$column' not found on line: ${rawRecord()}")
        return temp
    }

    fun readOpt(column : String) : String? {
        val temp = reader[column]
        return if(temp.isNullOrEmpty()) null else temp
    }

    fun rawRecord() : String = reader.rawRecord

    fun debug(msg: String) {
        log.debug(msg)
    }

    private fun info(msg: String) {
        log.info(msg)
    }

    private fun warn(msg: String) {
        log.warn(msg)
    }

    private fun validateHeader(expHeaders: Set<String>) {
        val actualHeaders = reader.headers.toSet()

        expHeaders.forEach{h ->
            if(!actualHeaders.contains(h)) {
                warn("Expected header missing in CSV file: '$h'.")
            }
        }
        actualHeaders.forEach{h ->
            if(!expHeaders.contains(h)) {
                warn("Actual header not expected in CSV file: '$h'.")
            }
        }
    }
}
