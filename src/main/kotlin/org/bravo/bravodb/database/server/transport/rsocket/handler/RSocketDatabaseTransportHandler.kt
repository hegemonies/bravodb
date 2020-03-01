package org.bravo.bravodb.database.server.transport.rsocket.handler

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.database.AddDataRequest
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import reactor.core.publisher.Mono

class RSocketDatabaseTransportHandler : AbstractRSocket() {

    override fun requestResponse(payload: Payload?): Mono<Payload> {
        return Mono.create { sink ->
            payload?.also {
                logger.info("Receive data: ${payload.dataUtf8}")

                val request = fromJson<Request>(payload.dataUtf8)
                when (request.type) {
                    DataType.ADD_DATA_REQUEST -> {
                        val requestBody = fromJson<AddDataRequest>(request.body)
                        // todo: Add data to DataStorage
                    }
                    else -> sink.error(Exception("Not correct datatype"))
                }
            } ?: sink.error(Exception("Payload is null"))
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
