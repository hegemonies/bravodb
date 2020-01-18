package org.bravo.bravodb

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bravo.bravodb.discovery.client.Client
import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.server.config.ServerConfig
import org.bravo.bravodb.discovery.server.Server
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer

fun main() {
    val port = 8919
    val config = ServerConfig(RSocketServer(), port)

    Server(config).start()

    val clientConfig = ClientConfig.port(port)
    runBlocking {
        Client(clientConfig).registration().awaitFirstOrNull()
    }
}
