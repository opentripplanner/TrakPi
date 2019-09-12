package org.opentripplanner.trakpi.application.csvimport

import java.io.File
import kotlin.system.exitProcess

internal class CmdLineArguments(args: List<String>) {
    private val file: File
    private val debug: Boolean
    private val clean: Boolean

    init {
        file = validateArgsReturnFile(args)
        debug = args.contains(OPT_DEBUG)
        clean = args.contains(OPT_CLEAN)
    }

    fun debugEnabled() = debug
    fun cleanBeforeImporting() = clean


    private fun validateArgsReturnFile(args: List<String>): File {
        if (args.isEmpty()) {
            exit(-1, "No argument exist!")
        }
        var tempFile: File? = null

        for (arg in args) {
            if (arg.startsWith("--")) {
                if (!OPTIONS.contains(arg)) {
                    exit(-2, "Option not recognized: $arg")
                }
            }
            else if (tempFile == null) {
                val f = File(arg)

                if (!f.exists()) {
                    exit(-3, "File do not exist: " + file.absolutePath)
                }

                if (f.isDirectory && listCsvFiles(f).isEmpty()) {
                    exit(-4, "No CSV file exist in: " + file.absolutePath)
                }
                tempFile = f
            }
            else {
                exit(-5, "Multiple arguments look like a path: '$arg' and '$tempFile'")
            }
        }
        checkNotNull(tempFile)
        return tempFile
    }

    fun listCsvFiles(): List<File> {
        return if (file.isDirectory) listCsvFiles(file) else arrayListOf(file)
    }

    private fun exit(code: Int, message: String) {
        printHelp()
        System.err.println("Error! $message")
        exitProcess(code)
    }

    private fun printHelp() {
        println(
            """
        |Usage: [--debug] <path>
        |    --clean : Delete existing database entities for each given import file. 
        |    --debug : Print debug info to standard out.
        |    <path>  : CSV file or directory with CSV files. The file name must be one of the following:
        |        🌼 "test_cases.csv"
        |        🌼 "test_plans.csv"
        |        🌼 "planners.csv"
        |The CSV file must use ',' as a delimiter and encoded as UTF-8.         
        """.trimMargin()
        )
    }

    companion object {
        private const val OPT_DEBUG = "--debug"
        private const val OPT_CLEAN = "--clean"
        private val OPTIONS = listOf(OPT_DEBUG, OPT_CLEAN)

        private fun listCsvFiles(dir: File): List<File> {
            return dir.listFiles { _, name -> name.endsWith(".csv") }?.toList() ?: emptyList()
        }
    }
}