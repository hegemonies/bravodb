package org.bravo.bravodb.discovery.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.server.config.ServerConfig

/**
 * Discovery server
 */
class Server(
    private val config: ServerConfig
) {

    private val transport = config.transport

    /**
     * Async start server
     */
    suspend fun start() {
        logger.info("Bootstrap discovery server start on port ${config.port}")
        GlobalScope.launch {
            bootstrap()
        }
    }

    private suspend fun bootstrap() {
        transport.start()
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
