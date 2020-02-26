package org.bravo.bravodb.discovery

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.Client
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import org.bravo.bravodb.data.transport.InstanceInfo
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.config.ServerConfig

class Discovery(
    private val clientConfig: ClientConfig,
    private val serverConfig: ServerConfig
) {
    private val server = Server(serverConfig)
    private val client = Client(clientConfig)

    fun start(otherServerConfig: ServerConfig) = runBlocking {
        logger.info("Discovery start")

        if (serverConfig::class.java != otherServerConfig::class.java) {
            logger.error("Type of server config and other known server config not equal: ${serverConfig::class.java} != ${otherServerConfig::class.java}")
            return@runBlocking
        }

        InstanceStorage.save(InstanceInfo(clientConfig.host, clientConfig.port))
        InstanceStorage.save(InstanceInfo(serverConfig.host, serverConfig.port))

        bootstrapServer()
        bootstrapClient(otherServerConfig)
        scheduleReregistration()
    }

    private suspend fun scheduleReregistration() {
        while (true) {
            delay(60 * 1000)
            InstanceStorage.instances.forEach { instance ->
                client.registrationIn(InstanceInfo(instance.host, instance.port))
                    .takeIf { it }.also {
                        logger.info("Reregistration in $instance is successful")
                    }
            }
        }
    }

    /**
     * Start server for to receive registration and to send known hosts
     */
    private suspend fun bootstrapServer() {
        logger.info("Bootstrap server")
        server.start()
    }

    /**
     * Do self registration on other same servers
     */
    private suspend fun bootstrapClient(otherServerConfig: ServerConfig) {
        logger.info("Bootstrap client")

        // registration and get info about other instance on first known instance
        val isRegistration = client.registrationIn(InstanceInfo(otherServerConfig.host, otherServerConfig.port))

        // registration in got instances
        if (isRegistration) {
            InstanceStorage.instances
                .filter {
                    it.host != DefaultConnectInfo.HOST && it.port != DefaultConnectInfo.PORT
                }
                .forEach {
                    if (!client.registrationIn(it)) {
                        logger.error("Can not registration in instance $it")
                    }
                }
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
