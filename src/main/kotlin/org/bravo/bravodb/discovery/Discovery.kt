package org.bravo.bravodb.discovery

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.Client
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.config.ServerConfig

class Discovery(
    private val selfConfig: ClientConfig,
    private val configOtherServer: ServerConfig
) {

    fun start() = runBlocking {
        logger.info("Discovery start")
        bootstrapServer()
        delay(1000)
        bootstrapClient()
    }

    /**
     * Start server for to receive registration and to send known hosts
     */
    private suspend fun bootstrapServer() {
        logger.info("Bootstrap server")
        Server(configOtherServer).start()
    }

    /**
     * Do self registration on other same servers
     */
    private suspend fun bootstrapClient() {
        logger.info("Bootstrap client")
        Client(selfConfig).registration()
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
