package org.bravo.bravodb

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.properties.DiscoveryProperties
import org.bravo.bravodb.discovery.server.config.ServerDiscoveryConfig
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServerDiscovery
import java.lang.Exception
import kotlin.system.measureTimeMillis

private val logger = LogManager.getLogger()

fun main() {
    try {
        initializeDiscovery()
    } catch (e: Exception) {
        logger.error("Error from main: ${e.message}")
    } catch (thr: Throwable) {
        logger.error("Error from main: ${thr.message}")
    }
}

fun initializeDiscovery() {
    measureTimeMillis {
        // val discoveryProperties = DiscoveryProperties.fromResourceFile("application.properties")
        //     ?: run {
        //         println("Can not read properties")
        //         return
        //     }
        val discoveryProperties = DiscoveryProperties.fromEnvironments()

        logger.info("Setup discovery properties: $discoveryProperties")

        val selfServerConfig = ServerDiscoveryConfig.Builder()
            .setPort(discoveryProperties.selfServerPort)
            .setHost(discoveryProperties.selfServerHost)
            .setTransport(RSocketServerDiscovery.javaClass)
            .build()

        val configOtherServer = if (discoveryProperties.otherServerHost != null
            && discoveryProperties.otherServerPort != null
        ) {
            ServerDiscoveryConfig.Builder()
                .setPort(discoveryProperties.otherServerPort)
                .setHost(discoveryProperties.otherServerHost)
                .setTransport(RSocketServerDiscovery.javaClass)
                .build()
        } else {
            null
        }

        configOtherServer?.let {
            Discovery(selfServerConfig).start(it)
        } ?: Discovery(selfServerConfig).start()
    }.also {
        logger.info("Discovery start by $it millis")
    }
}
