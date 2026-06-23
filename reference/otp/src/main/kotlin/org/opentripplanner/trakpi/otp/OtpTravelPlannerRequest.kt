package org.opentripplanner.trakpi.otp

import org.opentripplanner.trakpi.tester.spi.TravelPlannerRequest

/** An OTP request: a GraphQL query to POST to the Transmodel API. */
data class OtpTravelPlannerRequest(val query: String) : TravelPlannerRequest
