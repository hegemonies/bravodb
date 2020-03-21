package org.bravo.bravodb.database.server.transport.rsocket.handler

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.database.SendDataUnit
import org.bravo.bravodb.data.storage.DataStorage
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.data.storage.model.DataUnit
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

                runCatching {
                    val request = fromJson<Request>(it)

                    if (request.type != DataType.SEND_DATA) {
                        logger.error("Received not correct datatype")
                        sink.error(Exception("Not correct datatype"))
                    } else {
                        val requestBody = fromJson<SendDataUnit>(request.body)
                        runBlocking {
                            DataStorage.save(requestBody.key, requestBody.value)
                        }
                        GlobalScope.launch {
                            replicationData(requestBody)
                        }.start()
                        val response = Response(Answer(AnswerStatus.OK))
                        sink.success(DefaultPayload.create(response.toJson()))
                    }
                }.getOrElse { error ->
                    sink.error(error)
                }
            } ?: sink.error(Exception("Payload is null"))
        }
    }

    private suspend fun replicationData(unitBody: SendDataUnit) {
        val data = DataUnit(unitBody.key, unitBody.value)
        InstanceStorage.findAll().asFlow().collect {
            it.client.sendData(data)
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
