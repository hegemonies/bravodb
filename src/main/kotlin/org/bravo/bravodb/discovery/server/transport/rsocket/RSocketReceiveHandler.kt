package org.bravo.bravodb.discovery.server.transport.rsocket

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono

class RSocketReceiveHandler : AbstractRSocket() {
    override fun requestResponse(payload: Payload?): Mono<Payload> {
        logger.info(payload?.dataUtf8 ?: "empty data :(")
        return Mono.just(DefaultPayload.create("Pong"))
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
