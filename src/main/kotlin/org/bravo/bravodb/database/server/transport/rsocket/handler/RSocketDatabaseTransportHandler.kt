package org.bravo.bravodb.database.server.transport.rsocket.handler

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono

class RSocketDatabaseTransportHandler : AbstractRSocket() {

    override fun requestResponse(payload: Payload?): Mono<Payload> {
        return Mono.create { sink ->
            logger.info("Receive data: ${payload?.dataUtf8}")

            payload ?: sink.error(Exception("Payload is null"))

            val request = 1
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
