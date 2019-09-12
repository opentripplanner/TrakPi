package org.opentripplanner.trakpi.store.framework


enum class DbCollection(val text: String) {
    MIGRATIONS("Migrations"),
    TEST_CASES("Test Cases"),
    TEST_PLANS("Test Plans"),
    PLANNERS("Planners"),
    TEST_RUNS("Test Runs");
}