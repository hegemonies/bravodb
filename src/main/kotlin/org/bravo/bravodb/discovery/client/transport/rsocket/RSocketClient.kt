package org.bravo.bravodb.discovery.client.transport.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.transport.ClientTransport
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
        (0..10).forEach {
            client?.run {
                requestResponse(DefaultPayload.create("Ping $it"))
                        .awaitFirstOrNull()
                        ?.also {
                            logger.info("Receive data: ${it.dataUtf8}")
                        }
            }
            ?: also {
                logger.error("Error self registration")
            }
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
