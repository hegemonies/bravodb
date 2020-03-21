package org.bravo.bravodb.discovery

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.config.ServerConfig

class Discovery(
    private val serverConfig: ServerConfig
) {
    private val server = Server(serverConfig)

    fun start(configOtherServer: ServerConfig) = runBlocking {
        logger.info("Discovery start")

        if (serverConfig::class.java != configOtherServer::class.java) {
            logger.error(
                "Type of server config and other known server config not equal:" +
                    " ${serverConfig::class.java} != ${configOtherServer::class.java}"
            )
            return@runBlocking
        }

        bootstrapServer()
        if (configOtherServer.host != serverConfig.host && configOtherServer.port != serverConfig.port) {
            InstanceStorage.save(
                configOtherServer.host,
                configOtherServer.port
            )
            firstRegistration(configOtherServer)
        }
        scheduleReregistration()
    }

    private suspend fun scheduleReregistration() {
        while (true) {
            delay(15 * 1000) // 15 seconds
            logger.info("Start re-registration")
            InstanceStorage.findAll().forEach { instance ->
                if (instance.client.registration()) {
                    logger.info("Reregistration in $instance is successfully")
                } else {
                    logger.error("Reregistration in $instance is bad")
                }
            }
            logger.info("Finish re-registration")
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
    private suspend fun firstRegistration(otherServerConfig: ServerConfig) {
        logger.info("First registration start")


        // registration and get info about other instance on first known instance
        val isRegistration = InstanceStorage.findByHost(otherServerConfig.host)?.client?.registration()
            ?: logger.info("Can not find $otherServerConfig").let {
                return
            }

        // registration in got instances
        if (isRegistration) {
            InstanceStorage.findAll().asFlow()
                .filter { instance ->
                    instance.host != DefaultConnectInfo.HOST && instance.port != DefaultConnectInfo.PORT
                        && instance.host != otherServerConfig.host && instance.port != otherServerConfig.port
                }
                .collect { instance ->
                    if (!instance.client.registration()) {
                        logger.error("Can not registration in instance ${instance.host}:${instance.port}")
                    }
                }
        } else {
            logger.error("Can not registration in instance ${otherServerConfig.host}:${otherServerConfig.port}")
        }
        logger.info("First registration start")
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
