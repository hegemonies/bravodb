package org.bravo.bravodb.database.server.transport.rsocket

import io.rsocket.ConnectionSetupPayload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.server.TcpServerTransport
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.database.server.transport.ServerDatabaseTransport
import org.bravo.bravodb.database.server.transport.rsocket.handler.RSocketDatabaseTransportHandler
import reactor.core.publisher.Mono

class RSocketServerDatabaseTransport(
    override val port: Int,
    override val host: String
) : ServerDatabaseTransport {

    override suspend fun start() {
        try {
            RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .acceptor(this::receiveHandler)
                .transport(TcpServerTransport.create(port))
                .start()
                .awaitFirstOrNull()
                ?.onClose()
                ?: logger.error("Error starting RSocket database server")
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    private fun receiveHandler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.just(RSocketDatabaseTransportHandler())
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
