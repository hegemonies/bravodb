package org.bravo.bravodb.client.transport.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.frame.decoder.PayloadDecoder
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
import org.bravo.bravodb.data.storage.model.InstanceInfoView
import org.bravo.bravodb.data.transport.AnswerStatus
import org.bravo.bravodb.data.transport.DataType
import org.bravo.bravodb.data.transport.Request
import org.bravo.bravodb.data.transport.Response
import java.time.Duration

class RSocketClient(
    override val host: String,
    override val port: Int
) : Client {

    private var client: RSocket? = null

    init {
        logger.info("Start connect to $host:$port")
        runBlocking {
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
        }
        logger.info("Finish connect to $host:$port")
    }

    override suspend fun connect(): Boolean {
        runCatching {
            client = RSocketFactory.connect()
                .keepAlive(Duration.ofSeconds(2), Duration.ofSeconds(2), 1)
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .transport(TcpClientTransport.create(host, port))
                .start()
                .awaitFirstOrNull()
        }.getOrElse {
            logger.error(it.message)
            return false
        }
        return client != null
    }

    override suspend fun registration(selfHost: String, selfPort: Int): Boolean {
        client ?: also {
            logger.error("Client of $host:$port is null")
            if (!connect()) {
                logger.error("Bad reconnection")
            } else {
                logger.info("Successfully reconnection")
            }
            return false
        }

        logger.info("Start registration in $host:$port")

        val requestBody = RegistrationRequest(
            InstanceInfoView(selfHost, selfPort)
        ).toJson()
        val request = Request(DataType.REGISTRATION_REQUEST, requestBody).toJson()

        client?.requestResponse(DefaultPayload.create(request))
            ?.awaitFirstOrNull()
            ?.also { payload ->
                logger.info("Received response: ${payload.dataUtf8}")
                val response = fromJson<Response>(payload.dataUtf8)

                if (response.answer.statusCode == AnswerStatus.OK) {
                    response.body ?: let {
                        logger.error("Response body is null")
                        return false
                    }

                    fromJson<RegistrationResponse>(response.body).also { resp ->
                        if (resp.otherInstances.count() > 0) {
                            resp.otherInstances.forEach { instanceInfo ->
                                if (!InstanceStorage.save(instanceInfo.host, instanceInfo.port)) {
                                    logger.info(
                                        "Cannot adding instance info ${instanceInfo.host}:${instanceInfo.port}" +
                                            " in storage because it already exists"
                                    )
                                }
                            }
                        } else {
                            logger.info("Received 0 other instances")
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
