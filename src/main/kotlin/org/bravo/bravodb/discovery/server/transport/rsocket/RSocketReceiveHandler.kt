package org.bravo.bravodb.discovery.server.transport.rsocket

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.AnswerStatus
import org.bravo.bravodb.discovery.data.common.DataType
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import org.bravo.bravodb.discovery.data.common.Request
import org.bravo.bravodb.discovery.data.common.Response
import org.bravo.bravodb.discovery.data.registration.RegistrationRequest
import org.bravo.bravodb.discovery.data.registration.RegistrationResponse
import org.bravo.bravodb.discovery.data.storage.InstanceStorage
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
                    val request = Request.fromJson(it.dataUtf8)
                    if (request.type != DataType.REGISTRATION_REQUEST) {
                        sink.error(Exception("Data type is not correct"))
                    }
                    val requestBody = RegistrationRequest.fromJson(request.body)
                    runBlocking {
                        InstanceStorage.save(InstanceInfo(requestBody.instanceInfo.host, requestBody.instanceInfo.port))
                        InstanceStorage.findAll().let { instancesInfo ->
                            Response(
                                Answer(AnswerStatus.OK),
                                DataType.REGISTRATION_RESPONSE,
                                RegistrationResponse(instancesInfo).toJson()
                            ).toJson().let { json ->
                                sink.success(DefaultPayload.create(json))
                            }
                        }
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
