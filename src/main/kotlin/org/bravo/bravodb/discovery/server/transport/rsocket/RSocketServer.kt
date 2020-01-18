package org.bravo.bravodb.discovery.server.transport.rsocket

import io.rsocket.*
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.server.TcpServerTransport
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.server.transport.ServerTransport
import reactor.core.publisher.Mono

class RSocketServer : ServerTransport {

    override suspend fun start(port: Int) {
        RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .acceptor(this::receiveHandler)
                .transport(TcpServerTransport.create(port))
                .start()
                .awaitFirstOrNull()
                ?.onClose()
                ?: logger.error("Error starting RSocket server")
    }

    private fun receiveHandler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.just(RSocketReceiveHandler())
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
