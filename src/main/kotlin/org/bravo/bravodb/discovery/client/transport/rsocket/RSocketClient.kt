package org.bravo.bravodb.discovery.client.transport.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.transport.ClientTransport
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import org.bravo.bravodb.discovery.data.common.AnswerStatus
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import org.bravo.bravodb.discovery.data.registration.Request
import org.bravo.bravodb.discovery.data.registration.Response
import org.bravo.bravodb.discovery.data.storage.InstanceStorage
import kotlin.system.exitProcess

class RSocketClient : ClientTransport {

    private var client: RSocket? = null
    override var port: Int = DefaultConnectInfo.PORT
    override var host: String = DefaultConnectInfo.HOST

    private suspend fun initClient(host: String, port: Int) {
        client = RSocketFactory.connect()
            .transport(TcpClientTransport.create(host, port))
            .start()
            .awaitFirstOrNull()
        client ?: also {
            logger.error("Error init RSocketClient")
            exitProcess(1)
        }
    }

    override suspend fun registrationIn(otherInstance: InstanceInfo): Boolean {
        client ?: initClient(otherInstance.host, otherInstance.port)

        val request = Request(InstanceInfo(host, port)).toJson()

        client?.run {
            requestResponse(DefaultPayload.create(request))
                .awaitFirstOrNull()
                ?.also { payload ->
                    Response.fromJson(payload.dataUtf8).also { response ->
                        if (response.status.statusCode == AnswerStatus.OK) {
                            response.otherInstances?.forEach { instanceInfo ->
                                if (!InstanceStorage.save(instanceInfo)) {
                                    logger.error("Error adding instance info in storage")
                                    return false
                                }
                            }
                                ?: also {
                                    logger.error("List of other instances in response on registration is null")
                                    return false
                                }
                        } else {
                            logger.error("Receive error status on self registration")
                            return false
                        }
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
