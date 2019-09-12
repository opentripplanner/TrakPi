package org.opentripplanner.trakpi.store.framework

import org.opentripplanner.trakpi.store.framework.migrations.A001_Initialize_Migrations

interface Migration {
    fun name() = this.javaClass.simpleName
    fun migrate()
}

/**
 * The MongoDB Schema is defined by the `org.opentripplanner.trakpi.model` classes. To be able
 * to load existing data from the store when the model is changed a new migration should
 * be added. The migration is run at startup - and only if the migration is not run before.
 *
 * Migrations are run in order during the server startup. Byt if a migration is run earlier,
 * and a new migration is listed before the other then, the new migration is run, but not the
 * "next one". This may happen as a result of merging in a feature branch.
 */
object Migrations {

    fun migrate() {
        val migrations = DbClient.getMigrations()

        MIGRATIONS.forEach {
            if(!migrations.names.contains(it.name())) {
                performMigration(it)
                migrations.names.add(it.name())
            }
        }
        DbClient.save(migrations)
    }

    private fun performMigration(m: Migration) {
        println("DB - Perform migration: " + m.name())
        m.migrate()
    }

    val MIGRATIONS = listOf<Migration>(
            A001_Initialize_Migrations()
    )
}