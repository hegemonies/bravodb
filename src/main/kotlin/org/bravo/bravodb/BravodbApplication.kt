package org.bravo.bravodb

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.properties.DiscoveryProperties
import org.bravo.bravodb.discovery.server.config.ServerDiscoveryConfig
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServerDiscovery
import kotlin.system.measureTimeMillis

private val logger = LogManager.getLogger()

fun main() {
    initializeDiscovery()
}

fun initializeDiscovery() {
    measureTimeMillis {
        val discoveryProperties = DiscoveryProperties.fromResourceFile("application.properties")
            ?: run {
                println("Can not read properties")
                return
            }

        val selfServerConfig = ServerDiscoveryConfig.Builder()
            .setPort(discoveryProperties.selfServerPort)
            .setHost(discoveryProperties.selfServerHost)
            .setTransport(RSocketServerDiscovery.javaClass)
            .build()

        val configOtherServer = ServerDiscoveryConfig.Builder()
            .setPort(discoveryProperties.otherServerPort)
            .setHost(discoveryProperties.otherServerHost)
            .setTransport(RSocketServerDiscovery.javaClass)
            .build()

        Discovery(selfServerConfig).start(configOtherServer)
    }.also {
        logger.info("Discovery start by $it millis")
    }
}
