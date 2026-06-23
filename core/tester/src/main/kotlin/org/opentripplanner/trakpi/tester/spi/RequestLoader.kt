package org.opentripplanner.trakpi.tester.spi

/** Turns a [RequestFile] into a planner-specific [TravelPlannerRequest]. */
interface RequestLoader<out R : TravelPlannerRequest> {
    fun load(file: RequestFile): R
}
