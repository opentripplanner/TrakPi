package org.opentripplanner.trakpi.application.routing.clients

import org.opentripplanner.trakpi.model.Planner
import org.opentripplanner.trakpi.model.PlannerAPI
import org.opentripplanner.trakpi.model.PlannerAPI.OTP1_REST
import org.opentripplanner.trakpi.model.PlannerAPI.OTP2_GTFS
import org.opentripplanner.trakpi.model.PlannerAPI.OTP2_TRANSMODEL
import org.opentripplanner.trakpi.model.TestProfile

object ClientFactory {
    fun create(api: PlannerAPI, planner: Planner, profile: TestProfile): RoutingClient {
        when (api) {
            OTP1_REST       -> return Otp1RestClient(planner, profile)
            OTP2_TRANSMODEL -> TODO("Implement support for OTP2 Transmodel API")
            OTP2_GTFS       -> TODO("Implement support for OTP2 GTFS GraphQL API")
        }
    }
}