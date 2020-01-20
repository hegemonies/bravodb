package org.bravo.bravodb.discovery

import kotlinx.coroutines.runBlocking
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.server.config.ServerConfig

class Discovery(
    private val selfConfig: ClientConfig,
    private val configOtherServer: ServerConfig
) {

    fun start() = runBlocking {
        bootstrapServer()
        bootstrapClient()
    }

    /**
     * Start server for to receive registration and to send known hosts
     */
    private suspend fun bootstrapServer() {
        configOtherServer.transport.start()
    }

    /**
     * Do self registration on other same servers
     */
    private suspend fun bootstrapClient() {
        selfConfig.transport.selfRegistration()
    }
}
