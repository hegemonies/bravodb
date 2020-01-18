package org.bravo.bravodb.discovery.client

import org.bravo.bravodb.discovery.client.config.ClientConfig
import reactor.core.publisher.Mono

class Client(
        config: ClientConfig
) {
    private val transport: ClientTransport = config.rsocketTransport()

    suspend fun registration(): Mono<Void> {
        transport.selfRegistration()
        return Mono.empty()
    }
}
