package org.bravo.bravodb.discovery.client.transport.rsocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.transport.ClientTransport
import org.bravo.bravodb.discovery.data.common.AnswerStatus
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import org.bravo.bravodb.discovery.data.registration.Request
import org.bravo.bravodb.discovery.data.registration.Response
import org.bravo.bravodb.discovery.data.storage.InstanceStorage
import kotlin.system.exitProcess

class RSocketClient : ClientTransport {

    private var client: RSocket? = null
    override var port: Int = 8919
    override var host: String = "localhost"

    init {
        runBlocking {
            client = RSocketFactory.connect()
                .transport(TcpClientTransport.create(port))
                .start()
                .awaitFirstOrNull()
            client ?: also {
                logger.error("Error init RSocketClient")
                exitProcess(1)
            }
        }
    }

    override suspend fun selfRegistration() {
        val jsonMapper = jacksonObjectMapper()
        val request = jsonMapper.writeValueAsString(Request(InstanceInfo(host, port)))

        client?.run {
            requestResponse(DefaultPayload.create(request))
                .awaitFirstOrNull()
                ?.also { payload ->
                    jsonMapper.readValue<Response>(payload.dataUtf8).also { response ->
                        if (response.status.status == AnswerStatus.OK) {
                            response.otherInstances?.forEach { instanceInfo ->
                                if (!InstanceStorage.save(instanceInfo)) {
                                    logger.error("Error adding instance info in storage")
                                }
                            }
                                ?: also {
                                    logger.error("List of other instances in response on registration is null")
                                }
                        } else {
                            logger.error("Receive error status on self registration")
                        }
                    }
                    logger.info("Response on self registration: ${payload.dataUtf8}")
                }
        }
            ?: also {
                logger.error("Error self registration")
            }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
