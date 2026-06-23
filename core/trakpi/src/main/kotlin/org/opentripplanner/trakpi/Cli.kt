package org.opentripplanner.trakpi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.associate
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import org.opentripplanner.trakpi.config.TrakpiConfigLoader
import org.opentripplanner.trakpi.orchestrator.Orchestrator
import org.opentripplanner.trakpi.tester.RequestFileLoader
import org.opentripplanner.trakpi.tester.Tester
import org.opentripplanner.trakpi.tester.spi.KPICalculator
import org.opentripplanner.trakpi.tester.spi.RequestLoader
import org.opentripplanner.trakpi.tester.spi.ResultsStorage
import org.opentripplanner.trakpi.tester.spi.TravelPlanner
import org.opentripplanner.trakpi.tester.spi.TravelPlannerRequest

/** Runs the trakpi command-line interface, wiring the CLI to the supplied planner implementations. */
fun <R : TravelPlannerRequest> runTrakpi(
    args: Array<String>,
    requestLoader: RequestLoader<R>,
    travelPlanner: TravelPlanner<R>,
    kpiCalculators: List<KPICalculator>,
    resultsStorage: ResultsStorage,
) {
    val orchestrator = Orchestrator()
    Trakpi()
        .subcommands(
            Prepare(orchestrator),
            Start(orchestrator),
            Stop(orchestrator),
            Test(requestLoader, travelPlanner, kpiCalculators, resultsStorage),
            Kpis(),
            Diff(),
        )
        .main(args)
}

internal class Trakpi : CliktCommand(name = "trakpi") {
    override fun help(context: Context) = "Measure travel planner quality using Key Performance Indicators."

    override fun run() = Unit
}

/** Base for commands that operate on a single planner version. */
internal abstract class VersionedCommand(name: String) : CliktCommand(name = name) {
    protected val version: String by option("--version", help = "Planner version label, e.g. a commit hash").required()
}

internal class Prepare(private val orchestrator: Orchestrator) : VersionedCommand("prepare") {
    override fun help(context: Context) = "Prepare a planner version for testing."

    private val plannerArgs: String? by option("--plannerargs", help = "Opaque arguments passed to the planner adapter")

    override fun run() = orchestrator.prepare(version, plannerArgs)
}

internal class Start(private val orchestrator: Orchestrator) : VersionedCommand("start") {
    override fun help(context: Context) = "Start a planner process."

    override fun run() = orchestrator.start(version)
}

internal class Stop(private val orchestrator: Orchestrator) : VersionedCommand("stop") {
    override fun help(context: Context) = "Stop a running planner process."

    override fun run() = orchestrator.stop(version)
}

internal class Test<R : TravelPlannerRequest>(
    private val requestLoader: RequestLoader<R>,
    private val travelPlanner: TravelPlanner<R>,
    private val kpiCalculators: List<KPICalculator>,
    private val resultsStorage: ResultsStorage,
) : VersionedCommand("test") {
    override fun help(context: Context) = "Run a test. Assumes the planner is running and ready."

    private val configFile: Path? by
        option("--config", help = "Path to a trakpi config file (.properties)").path(mustExist = true, canBeDir = false)
    private val overrides: Map<String, String> by
        option("--set", help = "Override a config value, e.g. --set requests.dir=<path> (repeatable)").associate()

    // TODO: --version is not yet used by the engine; it will select the prepared planner build.
    override fun run() {
        val config =
            try {
                TrakpiConfigLoader.load(configFile = configFile, overrides = overrides)
            } catch (e: IllegalArgumentException) {
                throw UsageError(e.message ?: "Invalid configuration")
            }
        Tester(
                requestFileLoader = RequestFileLoader(config.requestsDir),
                requestLoader = requestLoader,
                travelPlanner = travelPlanner,
                kpiCalculators = kpiCalculators,
                resultsStorage = resultsStorage,
            )
            .run()
    }
}

internal class Kpis : VersionedCommand("kpis") {
    override fun help(context: Context) = "Show the KPIs for a planner version."

    // TODO: analysis command. Its module home is not yet decided (orchestrator, or a future
    //  dedicated analysis module), so it stays a placeholder here for now.
    override fun run() = echo("TODO: kpis $version")
}

internal class Diff : VersionedCommand("diff") {
    override fun help(context: Context) = "Compare KPIs against a baseline version."

    private val baseline: String by option("--baseline", help = "Baseline version to compare against").required()

    // TODO: analysis command. See the note on Kpis about where this should ultimately live.
    override fun run() = echo("TODO: diff $version against $baseline")
}
