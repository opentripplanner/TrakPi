package org.opentripplanner.trakpi.tester.spi

/** The raw contents of a request file, identified by [id]. */
data class RequestFile(val id: String, val body: String)
