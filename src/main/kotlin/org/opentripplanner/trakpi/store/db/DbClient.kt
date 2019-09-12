package org.opentripplanner.trakpi.store.framework

import com.mongodb.client.MongoCollection
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.opentripplanner.trakpi.store.framework.DbCollection.MIGRATIONS
import org.opentripplanner.trakpi.store.framework.DbCollection.PLANNERS
import org.opentripplanner.trakpi.store.framework.DbCollection.TEST_CASES
import org.opentripplanner.trakpi.store.framework.DbCollection.TEST_PLANS
import org.opentripplanner.trakpi.store.framework.DbCollection.TEST_RUNS
import org.opentripplanner.trakpi.store.framework.logging.Log
import org.opentripplanner.trakpi.store.schema.DbMigrations
import org.opentripplanner.trakpi.store.schema.DbNamedEntity
import org.opentripplanner.trakpi.store.schema.DbPlanner
import org.opentripplanner.trakpi.store.schema.DbTestCase
import org.opentripplanner.trakpi.store.schema.DbTestPlan
import org.opentripplanner.trakpi.store.schema.DbTestRun

/**
 * This class handle the MongoDb connection - only one connection is created.
 *
 * It have utility methods for accessing ALL DB collections(tables).
 */
object DbClient {
    private val log = Log("DB Client")

    private val client = KMongo.createClient()
    private val database = client.getDatabase("otp-trakpi-store")


    /* migrations */

    private fun migrations(): MongoCollection<DbMigrations> {
        return database.getCollection<DbMigrations>(MIGRATIONS.text)
    }
    fun getMigrations() = migrations().findOne() ?: DbMigrations()
    fun save(col: DbMigrations) = migrations().save(col)


    /* test cases */

    private fun testCases(): MongoCollection<DbTestCase> {
        return database.getCollection<DbTestCase>(TEST_CASES.text)
    }
    fun getTestCases() = list(testCases())
    fun save(tc: DbTestCase) = save(tc, testCases())
    fun deleteAllTestCases() = deleteAll(TEST_CASES)


    /* test plans */

    private fun testPlans(): MongoCollection<DbTestPlan> {
        return database.getCollection<DbTestPlan>(TEST_PLANS.text)
    }
    fun getTestPlans(): List<DbTestPlan> = list(testPlans())

    fun save(testPlan: DbTestPlan) {
        save(testPlan, testPlans())
    }
    fun deleteAllTestPlans() = deleteAll(TEST_PLANS)


    /* planners */

    private fun planners(): MongoCollection<DbPlanner> {
        return database.getCollection<DbPlanner>(PLANNERS.text)
    }
    fun getPlanners() = list(planners())
    fun save(p: DbPlanner) = save(p, planners())
    fun deleteAllPlanners() = deleteAll(PLANNERS)


    /* test runs */

    fun testRuns(): MongoCollection<DbTestRun> {
        return database.getCollection<DbTestRun>(TEST_RUNS.text)
    }
    fun deleteAllTestRuns() = deleteAll(TEST_RUNS)


    /* private methods */

    private fun <T : DbNamedEntity> list(col : MongoCollection<T>): List<T> {
        return col.find().toMutableList()
    }

    private fun <T : DbNamedEntity> save(entity: T, col : MongoCollection<T>) {
        val existingEntity = col.findOne("{name: '${entity.name}'}")

        if(existingEntity != null) {
            entity._id = existingEntity._id
        }
        col.save(entity)
        log.debug("Saved: ${entity}")
    }

    private fun deleteAll(collection : DbCollection) {
        database.getCollection(collection.name).drop()
        log.debug("All ${collection.name} deleted.")
    }
}