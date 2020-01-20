package org.bravo.bravodb

import org.bravo.bravodb.discovery.Discovery
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient
import org.bravo.bravodb.discovery.server.config.ServerConfig
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer

fun main() {
    val port = 8919

    val knownServerConfig = ServerConfig.Builder()
        .setPort(port)
        .setHost("localhost")
        .setTransport(RSocketServer())
        .build()

    val selfConfig = ClientConfig.Builder()
        .setPort(port)
        .setHost("localhost")
        .setTransport(RSocketClient())
        .build()

    Discovery(selfConfig, knownServerConfig).start()
}
