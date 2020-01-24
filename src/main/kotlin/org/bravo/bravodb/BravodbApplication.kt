package org.bravo.bravodb

import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient
import org.bravo.bravodb.discovery.properties.DiscoveryProperties
import org.bravo.bravodb.discovery.server.config.ServerConfig
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer

fun main() {
    val discoveryProperties = DiscoveryProperties.fromResourceFile("application.properties")
        ?: run {
            println("Can not read properties")
            return
        }

    val selfServerConfig = ServerConfig.Builder()
        .setPort(discoveryProperties.selfServerPort)
        .setHost(discoveryProperties.selfServerHost)
        .setTransport(RSocketServer())
        .build()

    val selfClientConfig = ClientConfig.Builder()
        .setPort(discoveryProperties.selfClientPort)
        .setHost(discoveryProperties.selfClientHost)
        .setTransport(RSocketClient())
        .build()

    Discovery(selfClientConfig, selfServerConfig).start(
        ServerConfig.Builder()
            .setPort(discoveryProperties.otherServerPort)
            .setHost(discoveryProperties.otherServerHost)
            .setTransport(RSocketServer())
            .build()
    )
}
