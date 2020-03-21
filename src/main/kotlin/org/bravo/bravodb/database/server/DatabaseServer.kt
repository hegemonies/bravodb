package org.bravo.bravodb.database.server

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.database.server.config.ServerDatabaseConfig

class DatabaseServer(
    private val config: ServerDatabaseConfig
) {

    fun start() = runBlocking {
        logger.info("Database server starting")
        config.transport.start()
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
