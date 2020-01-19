package org.bravo.bravodb.discovery.server.transport.rsocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.AnswerStatus
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import org.bravo.bravodb.discovery.data.registration.Request
import org.bravo.bravodb.discovery.data.registration.Response
import org.bravo.bravodb.discovery.data.storage.InstanceStorage
import reactor.core.publisher.Mono

class RSocketReceiveHandler : AbstractRSocket() {

    /**
     * Registration handler: save instance info in storage and response
     * @param [payload] contain data about host instance (InstanceInfo}
     */
    override fun requestResponse(payload: Payload?): Mono<Payload> {
        return Mono.create { sink ->
            logger.debug("Receive data: ${payload?.dataUtf8}")

            payload?.let {
                try {
                    val request: Request = jacksonObjectMapper().readValue(it.dataUtf8)
                    runBlocking {
                        InstanceStorage.save(InstanceInfo(request.instanceInfo.host, request.instanceInfo.port))
                        InstanceStorage.findAll().let { instancesInfo ->
                            jacksonObjectMapper().writeValueAsString(Response(Answer(AnswerStatus.OK), instancesInfo))
                                .let { json ->
                                    sink.success(DefaultPayload.create(json))
                                }
                        }
                    }
                } catch (e: Exception) {
                    sink.error(e)
                }
            }
                ?: "Receive empty payload".also {
                    logger.error(it)
                    sink.error(Exception(it))
                }
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
