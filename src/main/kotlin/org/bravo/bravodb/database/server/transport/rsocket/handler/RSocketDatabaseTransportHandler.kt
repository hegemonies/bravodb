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
import org.bravo.bravodb.data.database.GetDataUnitRequest
import org.bravo.bravodb.data.database.GetDataUnitResponse
import org.bravo.bravodb.data.database.PutDataUnit
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

                    when (request.type) {
                        DataType.PUT_DATA -> {
                            val requestBody = fromJson<PutDataUnit>(request.body)
                            runBlocking { DataStorage.save(requestBody.key, requestBody.value) }
                            GlobalScope.launch { replicationData(requestBody) }.start()
                            val response = Response(Answer(AnswerStatus.OK)).toJson()
                            sink.success(DefaultPayload.create(response)).also {
                                logger.info("Success answer $response")
                            }
                        }
                        DataType.GET_DATA -> {
                            val requestBody = fromJson<GetDataUnitRequest>(request.body)
                            val responseBody = GetDataUnitResponse(DataStorage.findByKey(requestBody.key)).toJson()
                            val response = Response(Answer(AnswerStatus.OK), DataType.GET_DATA, responseBody).toJson()
                            sink.success(DefaultPayload.create(response)).also {
                                logger.info("Success answer $response")
                            }
                        }
                        else -> {
                            logger.error("Received not correct datatype")
                            sink.error(Exception("Not correct datatype"))
                        }
                    }
                }.getOrElse { error ->
                    logger.error("Getting error during handling request: ${error.message}")
                    sink.error(error)
                }
            } ?: "Payload is null".let {
                logger.error(it)
                sink.error(Exception(it))
            }
        }
    }

    private suspend fun replicationData(unitBody: PutDataUnit) {
        val data = DataUnit(unitBody.key, unitBody.value)
        InstanceStorage.findAll().asFlow().collect {
            it.client.putData(data)
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
