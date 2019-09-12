package org.opentripplanner.trakpi.domain.model

import spock.lang.Specification
import spock.lang.Unroll

import static org.opentripplanner.trakpi.domain.model.TimeKt.durationToString


class DurationStringBuilderTest extends Specification {

    @Unroll
    def "Duration converted to string: #seconds -> #expected"() {
      expect:
        durationToString(seconds).toString() == expected
      where:
        seconds || expected
            0   || "0s"
           12   || "12s"
           64   || "1m4s"
          120   || "2m"
         3600   || "1h"
         3660   || "1h1m"
         3664   || "1h1m4s"
        86400   || "1d"
        86402   || "1d2s"
        86460   || "1d1m"
        90000   || "1d1h"
        86465   || "1d1m5s"
        90007   || "1d1h7s"
        90120   || "1d1h2m"
        90125   || "1d1h2m5s"
          -12   || "-12s"
       -90125   || "-1d1h2m5s"
    }
}
