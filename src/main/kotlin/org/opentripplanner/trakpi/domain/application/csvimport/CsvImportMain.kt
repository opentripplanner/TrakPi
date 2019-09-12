package org.opentripplanner.trakpi.domain.application.csvimport

import org.opentripplanner.trakpi.store.framework.DbClient
import org.opentripplanner.trakpi.store.framework.DbCollection
import org.opentripplanner.trakpi.store.framework.DbCollection.PLANNERS
import org.opentripplanner.trakpi.store.framework.DbCollection.TEST_CASES
import org.opentripplanner.trakpi.store.framework.DbCollection.TEST_PLANS
import org.opentripplanner.trakpi.store.framework.logging.Log
import org.opentripplanner.trakpi.store.schema.DbPlanner
import org.opentripplanner.trakpi.store.schema.DbTestCase
import org.opentripplanner.trakpi.store.schema.DbTestPlan
import java.io.File

private val LOG = Log("CSV IMPORT")

fun main(vararg args: String) {
    val cmd = CmdLineArguments(args.toList())

    Log.debugEnabled = cmd.debugEnabled()

    cmd.listCsvFiles().forEach { loadFile(it, cmd.cleanBeforeImporting()) }
}

private fun loadFile(csvFile: File, cleanBeforeImport: Boolean) {
    val aggregate = resolveAggregate(csvFile)

    if (aggregate == null) {
        LOG.warn("File is skipped: " + csvFile.absolutePath)
        return
    }

    val parser = CsvParser(csvFile)

    when (aggregate) {
        TEST_CASES -> {
            if(cleanBeforeImport) DbClient.deleteAllTestCases()
            for (it in ParseTestCases(parser).parse()) {
                DbClient.save(DbTestCase(it))
            }
        }
        TEST_PLANS -> {
            if(cleanBeforeImport) DbClient.deleteAllTestPlans()
            for (it in ParseTestPlans(parser).parse()) {
                DbClient.save(DbTestPlan(it))
            }
        }
        PLANNERS -> {
            if(cleanBeforeImport) DbClient.deleteAllPlanners()
            for (it in ParsePlanners(parser).parse()) {
                DbClient.save(DbPlanner(it))
            }
        }
        else -> {
            println("Import of ${aggregate.text} is not supported. File is skipped: ${csvFile.absolutePath}")
        }
    }
}


private fun resolveAggregate(file: File): DbCollection? {
    val fname = normalizedFileName(file.name)

    return DbCollection.values().find {
        "${normalizedFileName(it.name)}.csv" == fname
    }
}

private fun normalizedFileName(fname: String) = fname.replace(Regex("[-_]"), "").toLowerCase()
