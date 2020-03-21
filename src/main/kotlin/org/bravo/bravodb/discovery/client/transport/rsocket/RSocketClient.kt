package org.bravo.bravodb.discovery.client.transport.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.registration.RegistrationRequest
import org.bravo.bravodb.data.registration.RegistrationResponse
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.data.storage.model.InstanceInfo
import org.bravo.bravodb.data.transport.AnswerStatus
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import org.bravo.bravodb.data.transport.Response
import org.bravo.bravodb.discovery.client.transport.ClientTransport
import kotlin.system.exitProcess

class RSocketClient : ClientTransport {

    private var client: RSocket? = null

    private suspend fun initClient(host: String, port: Int) {
        client = RSocketFactory.connect()
            .transport(TcpClientTransport.create(host, port))
            .start()
            .awaitFirstOrNull()
        client ?: also {
            logger.error("Error init RSocketClient during connection to $host:$port")
            // exitProcess(1)
        }
    }

    override suspend fun registration(host: String, port: Int): Boolean {
        client ?: initClient(host, port)

        val requestBody = RegistrationRequest(
            InstanceInfo(
                host,
                port
            )
        ).toJson()
        val request = Request(DataType.REGISTRATION_REQUEST, requestBody).toJson()

        client?.run {
            requestResponse(DefaultPayload.create(request))
                .awaitFirstOrNull()
                ?.also { payload ->
                    val response = fromJson<Response>(payload.dataUtf8)
                    if (response.answer.statusCode == AnswerStatus.OK) {
                        response.body ?: let {
                            logger.error("Response body is null")
                            return false
                        }
                        fromJson<RegistrationResponse>(response.body).also { resp ->
                            resp.otherInstances?.forEach { instanceInfo ->
                                if (!InstanceStorage.save(instanceInfo)) {
                                    logger.error("Error adding instance info in storage")
                                    return false
                                }
                            } ?: also {
                                logger.error("List of other instances in response on registration is null")
                                return false
                            }
                        }
                    } else {
                        logger.error("Receive error status on self registration")
                        return false
                    }
                    logger.info("Response on self registration: ${payload.dataUtf8}")
                }
        }
            ?: also {
                logger.error("Error self registration")
                return false
            }
        return true
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
