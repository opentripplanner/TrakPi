package org.opentripplanner.trakpi.otp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** Reads the OTP `trip` object out of a raw response body. */
internal fun TravelPlannerResponse.tripObject(): JsonObject? =
    Json.parseToJsonElement(raw).jsonObject["data"]?.jsonObject?.get("trip")?.jsonObject

/** The trip patterns (itineraries) of an OTP `trip` response, empty when there are none. */
internal fun TravelPlannerResponse.tripPatterns(): List<JsonObject> =
    (tripObject()?.get("tripPatterns") as? JsonArray)?.map { it.jsonObject } ?: emptyList()
