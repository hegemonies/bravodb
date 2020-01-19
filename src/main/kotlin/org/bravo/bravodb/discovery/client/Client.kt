package org.bravo.bravodb.discovery.client

import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.ClientTransport
import reactor.core.publisher.Mono

class Client(
        config: ClientConfig
) {
    private val transport: ClientTransport = config.transport

    suspend fun registration(): Mono<Void> {
        transport.selfRegistration()
        return Mono.empty()
    }
}
