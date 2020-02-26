package org.bravo.bravodb

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient
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

        val selfClientConfig = ClientConfig.Builder()
            .setPort(discoveryProperties.selfClientPort)
            .setHost(discoveryProperties.selfClientHost)
            .setTransport(RSocketClient.javaClass)
            .build()

        Discovery(selfClientConfig, selfServerConfig).start(
            ServerConfig.Builder()
                .setPort(discoveryProperties.otherServerPort)
                .setHost(discoveryProperties.otherServerHost)
                .setTransport(RSocketServer.javaClass)
                .build()
        )
    }.also {
        logger.info("Discovery start by $it millis")
    }
}
