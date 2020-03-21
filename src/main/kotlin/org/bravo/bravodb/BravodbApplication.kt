package org.bravo.bravodb

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.properties.DiscoveryProperties
import org.bravo.bravodb.discovery.server.config.ServerConfig
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer
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

        val selfServerConfig = ServerConfig.Builder()
            .setPort(discoveryProperties.selfServerPort)
            .setHost(discoveryProperties.selfServerHost)
            .setTransport(RSocketServer.javaClass)
            .build()

        val configOtherServer = ServerConfig.Builder()
            .setPort(discoveryProperties.otherServerPort)
            .setHost(discoveryProperties.otherServerHost)
            .setTransport(RSocketServer.javaClass)
            .build()

        Discovery(selfServerConfig).start(configOtherServer)
    }.also {
        logger.info("Discovery start by $it millis")
    }
}
