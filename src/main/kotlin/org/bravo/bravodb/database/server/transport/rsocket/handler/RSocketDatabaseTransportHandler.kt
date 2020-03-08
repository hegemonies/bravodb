package org.bravo.bravodb.database.server.transport.rsocket.handler

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.database.AddDataRequest
import org.bravo.bravodb.data.storage.DataStorage
import org.bravo.bravodb.data.transport.Answer
import org.bravo.bravodb.data.transport.AnswerStatus
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import org.bravo.bravodb.data.transport.Response
import reactor.core.publisher.Mono

class RSocketDatabaseTransportHandler : AbstractRSocket() {

    override fun requestResponse(payload: Payload?): Mono<Payload> {
        return Mono.create { sink ->
            payload?.dataUtf8?.also {
                logger.info("Receive data: $it")

                val request = fromJson<Request>(it)
                when (request.type) {
                    DataType.ADD_DATA_REQUEST -> {
                        val requestBody = fromJson<AddDataRequest>(request.body)
                        runBlocking {
                            if (!DataStorage.save(requestBody.key, requestBody.value)) {
                                sink.error(Exception("Can not save data"))
                            }
                        }
                        val response = Response(Answer(AnswerStatus.OK))
                        sink.success(DefaultPayload.create(response.toJson()))
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
