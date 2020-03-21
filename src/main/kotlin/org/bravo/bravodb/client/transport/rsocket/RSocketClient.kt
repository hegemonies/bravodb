package org.bravo.bravodb.client.transport.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.client.transport.Client
import org.bravo.bravodb.data.common.fromJson
import org.bravo.bravodb.data.database.SendDataUnit
import org.bravo.bravodb.data.registration.RegistrationRequest
import org.bravo.bravodb.data.registration.RegistrationResponse
import org.bravo.bravodb.data.storage.InstanceStorage
import org.bravo.bravodb.data.storage.model.DataUnit
import org.bravo.bravodb.data.storage.model.InstanceInfo
import org.bravo.bravodb.data.transport.AnswerStatus
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import org.bravo.bravodb.data.transport.Response

class RSocketClient(
    override val host: String,
    override val port: Int
) : Client {

    private var client: RSocket? = null

    init {
        runBlocking {
            runCatching {
                if (!connect()) {
                    logger.error("Error init RSocketClient during connection to $host:$port")
                    InstanceStorage.findByHostAndPort(host, port)?.also {
                        if (InstanceStorage.delete(it)) {
                            logger.info("Deleted instance $host:$port")
                        } else {
                            logger.info("Cannot delete instance $host:$port")
                        }
                    }
                }
            }.getOrElse {
                logger.error("Cannot connect to $host:$port: ${it.message}")
            }
        }
    }

    override suspend fun connect(): Boolean {
        client = RSocketFactory.connect()
            .transport(TcpClientTransport.create(host, port))
            .start()
            .awaitFirstOrNull()
        return client != null
    }

    override suspend fun registration(): Boolean {
        client ?: return false

        val requestBody = RegistrationRequest(
            InstanceInfo(host, port)
        ).toJson()
        val request = Request(DataType.REGISTRATION_REQUEST, requestBody).toJson()

        client?.requestResponse(DefaultPayload.create(request))
            ?.awaitFirstOrNull()
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
            ?: also {
                logger.error("Error registration")
                return false
            }
        return true
    }

    override suspend fun sendData(unit: DataUnit) {
        val requestBody = SendDataUnit(unit.key, unit.value).toJson()
        val request = Request(DataType.SEND_DATA, requestBody).toJson()

        val payload = client?.requestResponse(DefaultPayload.create(request))?.awaitFirstOrNull()?.dataUtf8
            ?: let {
                logger.error("Cannot send data to $host:$port: client not connection")
                return
            }

        val response = fromJson<Response>(payload)
        if (response.answer.statusCode != AnswerStatus.OK) {
            logger.error("Received not success response: ${response.answer.message}")
        }
        if (response.type != DataType.SEND_DATA) {
            logger.error("Data type in response is not DataType.SEND_DATA_RESPONSE")
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
