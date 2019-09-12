package org.opentripplanner.trakpi.domain.model

import spock.lang.Specification
import spock.lang.Unroll

class DistanceTest extends Specification {
    @Unroll
    def "ToString of #mm is #expected"() {

        expect:
        new Distance(mm).toString() == expected

        where:
        mm       || expected
              0  || "0m"
              1  || "0.00m"
              4  || "0.00m"
              5  || "0.01m"
             10  || "0.01m"
             94  || "0.09m"
             95  || "0.10m"
            100  || "0.10m"
            994  || "0.99m"
            995  || "1.00m"
            999  || "1.00m"
          1_000  || "1m"
          1_001  || "1.0m"
          1_049  || "1.0m"
          1_050  || "1.1m"
          9_949  || "9.9m"
          9_950  || "10.0m"
         10_000  || "10m"
         10_499  || "10m"
         10_500  || "11m"
        100_000  || "100m"
             -0  || "0m"
             -1  || "-0.00m"
        -10_000  || "-10m"
    }
}
