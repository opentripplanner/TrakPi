package org.opentripplanner.trakpi.application

import org.junit.Ignore
import spock.lang.Specification

@Ignore("This is a manual integration test - The MongoDb needs to be started befor this test is run.")
class DomainServiceTest extends Specification {

    def "A walk in the park"() {
        given:
        def plan = DomainService.INSTANCE.listTestPlans().first()


        expect:
            !plan.testCases.isEmpty()
    }
}
