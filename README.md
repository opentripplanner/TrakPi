# Trakπ

Trakπ is a library and CLI tool for testing and tracking quality of travel planners. It can be used to track
regressions, performance or any other _key performance indicators_ (KPIs) you might need. The tool is developed to be
entirely generic, which means you can use it to test any travel planner you like, provided you implement the necessary
adapters. Trakπ exposes a small SPI for this purpose, and includes a command line interface, so to use it, include the
library in your project, implement the adapters and wire up the CLI. A reference implementation made for testing
[OpenTripPlanner](https://github.com/opentripplanner/OpenTripPlanner) is provided in reference/otp.

## User guide
TODO

## Development
Requires JDK 25+ and Maven. The repository holds three independent builds — `core` (the library),
`storage`, and `reference/otp`. The downstream builds resolve `core` from your local Maven
repository, so build `core` first:

```bash
mvn -f core/pom.xml install
mvn -f storage/file/pom.xml package
mvn -f reference/otp/pom.xml package
```

Test a single core module with `-pl`:

```bash
mvn -f core/pom.xml -pl tester test
```

### Project layout
The repository contains three independent Maven builds: `core` is a multi-module reactor; `storage`
and `reference/otp` are standalone builds that depend on the published `core`.

```
core/
  tester          runs tests against an already-started planner
  orchestrator    prepares, starts and stops the planner
  trakpi          the library: command-line surface and public entry point (runTrakpi)
storage/
  file            file-based ResultsStorage adapter
reference/
  otp             executable reference implementation for OpenTripPlanner
```

### Running the OTP reference
To use the OTP reference (`reference/otp`) with a local build of trakpi.core, build `core` first (`mvn -f core/pom.xml install`), then see [README](reference/otp/README.md).

## Goals

The goal of Trakπ is to measure _travel planner_ quality. We want to build a general tool to issue travel planning requests and to build a history of planning results to analyze. 

What is the _best_ itinerary in a given case is subjective. Therefore; We need to come up with a good way of comparing results in an objective way. The idea is to develop a set of _Key Performance Indicators (KPIs)_. With KPIs we can compare any set of travel planning results.


## Key Performance Indicators (KPIs)
We can compute KPIs for each test case and then compare average and standard deviation for each test run in a selected set of samples. Here is a list of possible KPIs that would be interesting
- Success based on dynamic criteria (dynamic criteria?)
- Number of itineraries returned (is that useful?)
  - Why: The timeline of development of itinerary counts can tell us if a change caused more or fewer itineraries.
  - This doesn't say anything about whether a change is good or bad, but it 
- Response time for successful requests (+)
- Contains the fastest alternative
- Contains the most cost effective alternative 
- % of pareto optimal results

## Use Cases
The tool can be used in many use cases/user scenarios: 
1. Regression testing, and monitoring quality over time <- pri 1
2. Performance testing, and monitoring response times over time <- pri 2
3. Tune Travel Planner configuration
4. Verify quality threshold in an integration chain(as part of continuous integration system)
5. Compare different travel planners
6. Compare special use cases like Accessibility, Mode-specific results, or Operator/Feed existence 

## Inputs
For a single run, the following inputs are required. They can be configured either with a configuration file or with
command-line parameters. Command-line parameters always override what is given in the configuration file.
1. Test cases (travel requests and more). Identified by an id.
2. Street and transit data
3. Planner (given by name)
4. Planner version (e.g. a specific commit hash)
5. Persistence configuration (e.g. a file path, db connection string, or cloud storage connection string)
6. Planner arguments (additional arguments passed through to the planner adapter for planner-specific behavior)
   - These are opaque arguments consumed by the planner adapter. Trakπ itself doesn't consider these.

## Usage - Running a test
```bash
# Prepares a version A for testing
trakpi prepare --version A --plannerargs "--street-data osm-data --transit-data netex-norway"

# Optional: Multiple prepare commands can be run separately with different plannerargs, to support multi-stage setups.
trakpi prepare --version A --plannerargs "--build-only"
trakpi prepare --version A --plannerargs "--build-street-graph-only --street-data osm-data"
trakpi prepare --version A --plannerargs "--build-transit-data-only --transit-data"

# Start a planner. Planners that start a running process must be started before testing
trakpi start --version A

# Run a test
trakpi test --version A

# Stop a running planner process
trakpi stop --version A
```

Running `trakpi test` without first running `trakpi start` triggers a full `start - test - stop` flow for convenience.

Only a single instance can be started at a time.

## Inputs - requests
Each test run executes a set or requests. These are loaded from a folder of text files as configured in the config file.
Each text file contains one planner request, and the filename (without extension) is treated as extension.
Trakpi handles loading the request files without considering how the request is formatted in the file, then hands the
raw request file contents to an `spi.RequestLoader` to parse the request in a format supported by the `spi.TravelPlanner`.

## Outputs
Each test run stores the following outputs for each test case
1. Full raw outputs from the planner
2. Outputs from the planner mapped into the standardized format. (See section on Standardized format)
3. KPIs

## Usage - Analyzing results
Each test run stores the full outputs

```bash
# Look at the KPIs for version A
trakpi kpis --version A

# Compare the KPIs of A and B
trakpi diff --version A --baseline B
```


## Writing a planner adapter
An adapter is needed for each planner you wish to test against. By default, Trakπ comes with an adapter for
[OpenTripPlanner](https://github.com/opentripplanner/OpenTripPlanner). 

A planner must implement the following SPI:
* Prepare: Accepts a list of plannerargs. No output.
* Start: No output.
* Stop: No output.
* Test: Outputs raw outputs from the planner in an opaque text format.
* Mapping:
  * From and to standardized input (request) format
  * From and to standardized output format
* KPI computation:
  * From standardized outputs
  * From raw outputs (default: computed from raw outputs mapped to standardized format)

## Domain language
[Transmodel](https://transmodel-cen.eu/) language is used throughout the project whenever transit-specific terminology
is needed.

## Standardized input/output format
A test run outputs individual planning results in a standardized format, allowing you to compare different planners even
if they have different output formats.


## Visualizing results
TODO. Grafana.

## Usage with git
TODO.

High level usecase:
- Compare a git commit hash with a given baseline. 
- Test a sequence of commits
- Bisect with a commit range

## Multiple dimensions, drill-down and averaging
There are multiple dimensions to consider in tracking performance. Take for instance the response time KPI:
This KPI can be analyzed by drilling down into a cross-section of the data:
- Across planning requests: With e.g. 1000 requests in the sample dataset, the KPI can be analyzed for a single timestamp.
    - Why? This can show e.g. how different types of requests impact the response time. Some requests are naturally more expensive for a planner to resolve and by looking only at a single timestamp, we can discover those differences.
- Across "time" (e.g. planner versions by commit hashes): Honing in on a single KPI, it can be analyzed over time, to see e.g. how a request that hits a bottleneck in the planner has developed over time after applying various optimizations.

In addition to drilling down into a single dimension, we can also apply an average (or other aggregation like p95 or p99) across a dimension, e.g. view how response time has developed over time in general.
