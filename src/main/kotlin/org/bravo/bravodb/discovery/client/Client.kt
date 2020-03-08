package org.bravo.bravodb.discovery.client

import org.bravo.bravodb.discovery.client.config.ClientConfig

class Client(
    val config: ClientConfig
) {

    suspend fun registration() = config.transport.registration(config.host, config.port)
}
