package org.bravo.bravodb.discovery.client.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.client.ClientTransport
import java.time.Instant
import java.util.*
import kotlin.system.exitProcess

class RSocketClient(port: Int) : ClientTransport {

    private var client: RSocket? = null

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
                requestResponse(DefaultPayload.create("Ping $it from ${Date.from(Instant.now())}"))
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
