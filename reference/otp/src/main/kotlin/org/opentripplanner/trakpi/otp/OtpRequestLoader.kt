package org.opentripplanner.trakpi.otp

import org.opentripplanner.trakpi.tester.spi.RequestFile
import org.opentripplanner.trakpi.tester.spi.RequestLoader

/** Reads a request file body as an OTP GraphQL query. */
class OtpRequestLoader : RequestLoader<OtpTravelPlannerRequest> {
    override fun load(file: RequestFile) = OtpTravelPlannerRequest(file.body)
}
