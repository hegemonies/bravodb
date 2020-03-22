package org.bravo.bravodb.discovery.server.transport.rsocket

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.registration.RegistrationRequest
import org.bravo.bravodb.data.registration.RegistrationResponse
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.data.transport.Answer
import org.bravo.bravodb.data.transport.AnswerStatus
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import org.bravo.bravodb.data.transport.Response
import reactor.core.publisher.Mono

class RSocketReceiveHandler : AbstractRSocket() {

    /**
     * Registration handler: save instance info in storage and response
     * @param [payload] contain data about host instance (InstanceInfo}
     */
    override fun requestResponse(payload: Payload?): Mono<Payload> {
        return Mono.create { sink ->
            logger.info("Receive data: ${payload?.dataUtf8}")

            payload?.let {
                try {
                    val request = fromJson<Request>(it.dataUtf8)

                    if (request.type == DataType.REGISTRATION_REQUEST) {
                        val requestBody = fromJson<RegistrationRequest>(request.body)
                        runBlocking {
                            InstanceStorage.save(
                                requestBody.instanceInfo.host,
                                requestBody.instanceInfo.port
                            )
                            InstanceStorage.findAll().map { instanceInfo ->
                                instanceInfo.toView()
                            }.let { instancesInfoViewList ->
                                Response(
                                    Answer(AnswerStatus.OK),
                                    DataType.REGISTRATION_RESPONSE,
                                    RegistrationResponse(instancesInfoViewList).toJson()
                                ).toJson().let { json ->
                                    sink.success(DefaultPayload.create(json))
                                }
                            }
                        }
                    } else {
                        sink.error(Exception("Data type is not correct"))
                    }
                } catch (e: Exception) {
                    sink.error(e)
                }
            } ?: "Receive empty payload".also {
                logger.error(it)
                sink.error(Exception(it))
            }
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
