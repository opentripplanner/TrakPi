
# WIP -- This is work in progress.

This project is not working, it is made public to allow anyone to contribute. Be aware that there 
are many missing parts that exist in other places. The plan is to copy these parts into this project
when we get there. Examples of this is:

 - The test results comparison, which is part of the SpeedTest in R5
 - ParetoSet, also R5
 - PKI computing grid - t2gran´s local machine... (sorry, needs cleanup before I can submit it)
 

# Goals

The goal of Trakπ is to measure _travel planner_ quality. We want to build a general tool to issue travel planning requests and to build a history of planning results witch we can analyze. 

What is the _best_ itinerary in a given case is subjective. Therefore; We need to come up with a good way of comparing results in an objective way. The idea is to develop a set of _Key Performance Indicators(KPIs)_. With KPIs we can compare any set of travel planning results.


## Key Performance Indicators (KPIs)

We can compute KPIs for each test case and then compare average and standard deviation for each test run in a selected set of samples. Here is a list of possible KPIs that would be interesting
  - Success based on dynamic criteria
  - Number of itineraries returned
  - Response time for successful requests
  - Contains the fastest alternative
  - Contains the most cost effective alternative
  - % of pareto optimal results


# Use Cases

The tool can be used in many use cases/user scenarios: 
  1. Tune Travel Planner configuration
  1. Monitor performance over time
  1. Monitor quality over time
  1. Verify quality threshold in an integration chain(as part of continuous integration system)
  1. Compare different travel planners
  1. Compare special use cases like Accessibility, Mode-specific results, or Operator/Feed existence 


# High level usage scenario

1. Define *test cases* with a travel requests and _tag_ each test case to group them together. 
1. Define *test plan(s)* - a named collection of test cases.
1. Define *planners* - a named travel planner instance.
1. Run a test plan and save test results. 
1. Analyze and visualize result
    1. Select test cases or test plan, and set of test runs
    1. Calculate performance indicators for all results
    1. Compare performance indicators graphically

# User Guide
See [User Guide](doc/UserGuide.md)

# Design

The core of Trakπ is the `model` (package). We follow DDD principles; Hence general business logic
goes into the model package - if possible. Use-case specific logic or logic spanning multiple
aggregates belong in the `application` package. The `application` uses the `DomainService` to 
access the `model` and the `DomainService` provide methods to store the model using the `store`. 
The `framework` package contains shared utilities and framework integration - do not put bussiness
logic here. Try to separate logic used by the application and integration logic into different 
packages.


## Model 

These are the most important building bricks of the testing framework:
- `Tag` A tag/label is used to categorize test cases and results.
- `TestCase` A named planning request and tags.
- `TestPlan` A named set of `TestCases` defined by a Tag expression.
- `Planner` A named url to a travel planner like ("Entur OTP QA, https://entur-qa.en-tur.no/otp")
- `TestProfile` A fixed set of parameters passed to a travel request in addition to the test case request parameters. 
- `TestResult` The result of a single test case request with `Itinerary` and some metadata like response time
- `TestRun` One execution of a `TestPlan` with `TestResults`


## Application Layers

The application consist of the following layers:
- The `model` is the core types with business logic described above. No dependencies to other 
project packages is allowed.
- The `store` is responsible for persistence. The dependency is inverted compared with regular
 multi-tier-architecture. The `store` depends on the `model` and is used by the `DomainService`(only).
- The `application` uses the `DomainService` to perform its use case specific tasks. 
- The `framework` package - containing x-cutting logic and framework integration. Try to separate
utilities and integration logic to avoid circular dependencies. The `framework` sub-packages may 
depend on the `model` and can be used by the `application`. If the `model` NEEDS to use any framework
 code, make sure to put the needed classes in a sub-package witch does not depend on the model, and
 remember to document it.
 

# Architecture

We try to honer Object Oriented(OO), Functional Programing(FP), Domain Driven Design(DDD) and a multi-layered architecture principles, as well as borrowing concepts from Data, context, and interaction(DCI). 

We divide the application into 3 modules:
 - `model` - The domain model containing general concepts and aggregate business rules. Dependencies to other modules are not allowed, and 3rd party libraries kept at a minimum.
- `store` - The store map the `model` to persistence storage. For now a very simple ´Log´ object is also part of this module.
- `application` The application business rules, or use cases put together. The application uses the `store` and build its use case specific business logic on top of the `model`.


# Tech

Trakπ is written in _Kotlin_ and with tests in Groovy using the Spock testing framework. We want to use up-to-date frameworks on the same platform as OTP - the Java platform - this will make it easy for OTP developers to maintain the tool.

_MongoDb_ is chosen as a database to be able to get quickly up and running. The tool should not depend too much on this, since it might be better to use a relational database for analyzes.

 
