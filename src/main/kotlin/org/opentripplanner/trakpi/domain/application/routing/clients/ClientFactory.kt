package org.opentripplanner.trakpi.domain.application.routing.clients

import org.opentripplanner.trakpi.domain.model.Planner
import org.opentripplanner.trakpi.domain.model.PlannerAPI
import org.opentripplanner.trakpi.domain.model.PlannerAPI.OTP1_REST
import org.opentripplanner.trakpi.domain.model.PlannerAPI.OTP2_GTFS
import org.opentripplanner.trakpi.domain.model.PlannerAPI.OTP2_TRANSMODEL
import org.opentripplanner.trakpi.domain.model.TestProfile

object ClientFactory {
    fun create(api: PlannerAPI, planner: Planner, profile: TestProfile): RoutingClient {
        when (api) {
            OTP1_REST       -> return Otp1RestClient(planner, profile)
            OTP2_TRANSMODEL -> TODO("Implement support for OTP2 Transmodel API")
            OTP2_GTFS       -> TODO("Implement support for OTP2 GTFS GraphQL API")
        }
    }
}