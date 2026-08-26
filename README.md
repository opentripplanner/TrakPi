# Trakπ

Trakπ is a library and CLI tool for testing and tracking the quality of travel planners. It can be used to track
regressions, performance or any other _key performance indicators_ (KPIs) you might need. The tool is developed to be
entirely generic, which means you can use it to test any travel planner you like, provided you implement the necessary
adapters. Trakπ exposes a small SPI for this purpose. It includes a command line interface, so to use it, include the
library in your project, implement the adapters, and wire up the CLI. A reference implementation made for testing
[OpenTripPlanner](https://github.com/opentripplanner/OpenTripPlanner) is provided in reference/otp.


### Why the name Trackπ?

- It is short for **Ttrack**ing **P**erformance **I**ndikators.
- Pi (π) in mathematics is the ratio of the circumference of a circle to its diameter. It does not change when the diameter changes, like the goal of the Trackπ tool - we want to measure "values/KPIs" that don't change but describe important aspects of the system under test.
 

## User guide
TODO

## Development
Requires JDK 25+ and Maven. The repository holds three independent builds — `core` (the library),
`storage`, and `reference/otp`. Use Maven to build the project:

```bash
mvn pom.xml install
```

Test a single core module with `-pl`:

```bash
mvn -f core/pom.xml -pl tester test
```

### Running the OTP reference
To use the OTP reference (`reference/otp`) with a local build of trakpi.core, build `core` first (`mvn -f core/pom.xml install`), then see [README](reference/otp/README.md).

## Goals

The goal of Trakπ is to measure _travel planner_ quality. We want to build a general tool to issue travel planning requests and to build a history of planning results to analyze. Many test tools validate the result by matching the result to an expected value. However, this approach does not scale well to a large number of test cases, and false positives are common with a high maintenance cost. This tool is designed to have zero test-case maintenance cost. Therefore;

- We instead compute _Key Performance Indicators (KPIs)_ for each test case/test run. These can be compared with a reference (production) and tracked over time. A KPI can be generic (response time, HTTP status code, number of-graphql-errors) or querry specific (minimum number of transfers in the result of a trip planning call, name matches when looking up entities, findXById).
- What is the _best_ itinerary in a given case is subjective. So, instead of comparing two itineraries, we can decompose it and compare key component values instead, like number-of-transfers, walk-distance, operator-spread, minimum-waiting-time, etc.
- By tracking KPIs over time, we can discover changes caused by bugs and changes in data. We can also meassure improvments in quality.


## Key Performance Indicators (KPIs)
We can compute KPIs for each test case and then compare average and standard deviation for each test run in a selected set of samples. Here is a list of possible KPIs that would be interesting
- Success based on dynamic criteria (dynamic criteria?)
- Number of transfers
- Walk distance
- Minimum waiting time
- Minimum travel time
- Earliest arrival time (after search start time)
- Latest departure time (arrive-by search)
- Number of errors (GraphQL errors)
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

## Usage - Analyzing results
Each `trakpi test` run writes its results grouped under a run id. Point `trakpi analyze` at a folder holding one or
more such runs to see how KPIs move over time, or to diff two versions. Where results are stored is entirely up to the
`spi.ResultsLoader` adapter — trakpi knows nothing about GitHub, cloud storage, or how the history was assembled. The
file loader takes its arguments as an opaque `--loaderargs` string, mirroring `prepare --plannerargs`.

```bash
# Trend: aggregate each KPI per run and print a time-ordered series
trakpi analyze --loaderargs "--results-dir results/"

# Diff: compare the latest run of one version against a baseline version
trakpi analyze --loaderargs "--results-dir results/" --version <sha> --baseline <sha>
```

When runs are produced by a CI job that uploads each run as a build artifact, assemble the history locally first. The
included helper does this for the OTP reference's GitHub Actions artifacts (it is the only GitHub-aware piece; trakpi
just reads the folder it produces):

```bash
scripts/download-artifacts.sh results/
trakpi analyze --loaderargs "--results-dir results/"
```

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

## Multiple dimensions, drill-down, and averaging
There are multiple dimensions to consider in tracking performance. Take for instance the response time KPI:
This KPI can be analyzed by drilling down into a cross-section of the data:
- Across planning requests: With e.g. 1000 requests in the sample dataset, the KPI can be analyzed for a single timestamp.
    - Why? This can show e.g. how different types of requests impact the response time. Some requests are naturally more expensive for a planner to resolve and by looking only at a single timestamp, we can discover those differences.
- Across "time" (e.g. planner versions by commit hashes): Honing in on a single KPI, it can be analyzed over time, to see e.g. how a request that hits a bottleneck in the planner has developed over time after applying various optimizations.

In addition to drilling down into a single dimension, we can also apply an average (or other aggregation like p95 or p99) across a dimension, e.g. view how response time has developed over time in general.
