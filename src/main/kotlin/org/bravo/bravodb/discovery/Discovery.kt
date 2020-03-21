package org.bravo.bravodb.discovery

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.discovery.consts.DefaultDiscoveryConnectInfo
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.config.ServerDiscoveryConfig

class Discovery(
    private val serverDiscoveryConfig: ServerDiscoveryConfig
) {
    private val server = Server(serverDiscoveryConfig)

    fun start(configOtherServerDiscovery: ServerDiscoveryConfig) = runBlocking {
        logger.info("Discovery start")

        if (serverDiscoveryConfig::class.java != configOtherServerDiscovery::class.java) {
            logger.error(
                "Type of server config and other known server config not equal:" +
                    " ${serverDiscoveryConfig::class.java} != ${configOtherServerDiscovery::class.java}"
            )
            return@runBlocking
        }

        bootstrapServer()
        if (configOtherServerDiscovery.host != serverDiscoveryConfig.host && configOtherServerDiscovery.port != serverDiscoveryConfig.port) {
            InstanceStorage.save(
                configOtherServerDiscovery.host,
                configOtherServerDiscovery.port
            )
            firstRegistration(configOtherServerDiscovery)
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
    private suspend fun firstRegistration(otherServerDiscoveryConfig: ServerDiscoveryConfig) {
        logger.info("First registration start")

        // registration and get info about other instance on first known instance
        val isRegistration = InstanceStorage.findByHostAndPort(
            otherServerDiscoveryConfig.host,
            otherServerDiscoveryConfig.port
        )?.client?.registration()
            ?: logger.info("Can not find $otherServerDiscoveryConfig").let {
                return
            }

        // registration in got instances
        if (isRegistration) {
            InstanceStorage.findAll().asFlow()
                .filter { instance ->
                    instance.host != DefaultDiscoveryConnectInfo.HOST && instance.port != DefaultDiscoveryConnectInfo.PORT
                        && instance.host != otherServerDiscoveryConfig.host && instance.port != otherServerDiscoveryConfig.port
                }
                .collect { instance ->
                    if (!instance.client.registration()) {
                        logger.error("Can not registration in instance ${instance.host}:${instance.port}")
                    }
                }
        } else {
            logger.error("Can not registration in instance ${otherServerDiscoveryConfig.host}:${otherServerDiscoveryConfig.port}")
        }
        logger.info("First registration start")
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
