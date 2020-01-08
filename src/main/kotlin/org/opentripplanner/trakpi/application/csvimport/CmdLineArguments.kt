package org.opentripplanner.trakpi.application.csvimport

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import java.io.File
import kotlin.system.exitProcess


internal class CmdLineArguments(args: Array<out String>) {
    private val basePath: File
    private val debug: Boolean
    private val clean: Boolean

    init {
        if (args.isEmpty()) {
            exit(-1, "No argument exist!")
        }
        if (args.last().startsWith("-")) {
            exit(-2, "Last argument must be a filename!")
        }

        val parser = DefaultParser()
        val cmd = parser.parse(options(), args)

        if(cmd.hasOption(OPT_HELP)) {
            printHelp()
            exitProcess(0)
        }

        debug = cmd.hasOption(OPT_DEBUG)
        clean = cmd.hasOption(OPT_CLEAN)
        basePath = getAndVerifyPath(args.last())
    }

    fun debugEnabled() = debug
    fun cleanBeforeImporting() = clean


    private fun getAndVerifyPath(filename: String): File {
        val f = File(filename)

        if (!f.exists()) {
            exit(-3, "File do not exist: " + basePath.absolutePath)
        }

        if (f.isDirectory && listCsvFiles(f).isEmpty()) {
            exit(-4, "No CSV file exist in: " + basePath.absolutePath)
        }
        return f
    }

    fun listCsvFiles(): List<File> {
        return if (basePath.isDirectory) listCsvFiles(basePath) else arrayListOf(basePath)
    }

    private fun exit(code: Int, message: String) {
        printHelp()
        System.err.println("Error! $message")
        exitProcess(code)
    }

    private fun printHelp() {
        val formatter = HelpFormatter()
        formatter.printHelp(
            "[options] <path>",
            "",
            options(),
            """
        |    <path>    CSV file or directory with CSV files. The file name must be
        |              one of the following:
        |              🌼 "test_cases.csv"
        |              🌼 "test_plans.csv"
        |              🌼 "planners.csv"
        |The CSV file must use ';' as a delimiter and encoded as UTF-8.         
        """.trimMargin()
        )
    }

    companion object {
        private const val OPT_DEBUG = "--debug"
        private const val OPT_CLEAN = "--clean"
        private const val OPT_HELP = "--help"

        private fun listCsvFiles(dir: File): List<File> {
            return dir.listFiles { _, name -> name.endsWith(".csv") }?.toList() ?: emptyList()
        }

        private fun options(): Options {
            val options = Options()
            options.addOption("D", "debug", false, "Print debug info to standard out.")
            options.addOption("h", "help", false, "Print command line options.")
            options.addOption("c", "clean", false, "Delete existing database entities for each given import file.")
            return options
        }
    }
}