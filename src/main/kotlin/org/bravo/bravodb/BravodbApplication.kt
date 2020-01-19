package org.bravo.bravodb

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bravo.bravodb.discovery.client.Client
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient
import org.bravo.bravodb.discovery.server.config.ServerConfig
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer

fun main() {
    val port = 8919
    val config = ServerConfig.Builder()
            .setPort(port)
            .setHost("localhost")
            .setTransport(RSocketServer())
            .build()

    Server(config).start()

    val clientConfig = ClientConfig.Builder()
            .setPort(port)
            .setHost("localhost")
            .setTransport(RSocketClient())
            .build()
    runBlocking {
        Client(clientConfig).registration().awaitFirstOrNull()
    }
}
