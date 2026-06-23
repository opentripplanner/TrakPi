package org.opentripplanner.trakpi.otp

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.opentripplanner.trakpi.tester.spi.TravelPlanner
import org.opentripplanner.trakpi.tester.spi.TravelPlannerResponse

/** Executes a request by POSTing its GraphQL query to an OTP Transmodel endpoint. */
class OTPTravelPlanner(
    private val endpoint: String,
    private val clientName: String,
) : TravelPlanner<OtpTravelPlannerRequest> {
    private val http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()

    override fun execute(request: OtpTravelPlannerRequest): TravelPlannerResponse {
        val body = buildJsonObject { put("query", JsonPrimitive(request.query)) }.toString()
        val httpRequest =
            HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("ET-Client-Name", clientName)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()
        val response = http.send(httpRequest, BodyHandlers.ofString())
        return TravelPlannerResponse(response.body())
    }
}
