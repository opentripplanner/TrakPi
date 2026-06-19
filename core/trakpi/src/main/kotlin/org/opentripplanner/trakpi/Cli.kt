package org.opentripplanner.trakpi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.opentripplanner.trakpi.orchestrator.Orchestrator
import org.opentripplanner.trakpi.tester.Tester

/** Assembles the engine and lifecycle and runs the trakpi command-line interface. */
fun runTrakpi(args: Array<String>) {
    val orchestrator = Orchestrator()
    val tester = Tester()
    Trakpi()
        .subcommands(
            Prepare(orchestrator),
            Start(orchestrator),
            Stop(orchestrator),
            Test(tester),
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

internal class Test(private val tester: Tester) : VersionedCommand("test") {
    override fun help(context: Context) = "Run a test. Assumes the planner is running and ready."

    override fun run() = tester.runTest(version)
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
