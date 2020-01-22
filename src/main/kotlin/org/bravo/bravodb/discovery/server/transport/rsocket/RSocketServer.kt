package org.bravo.bravodb.discovery.server.transport.rsocket

import io.rsocket.ConnectionSetupPayload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.server.TcpServerTransport
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import org.bravo.bravodb.discovery.server.transport.ServerTransport
import reactor.core.publisher.Mono

class RSocketServer : ServerTransport {

    override var port: Int = DefaultConnectInfo.PORT
    override var host: String = DefaultConnectInfo.HOST

    override suspend fun start() {
        try {
            RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .acceptor(this::receiveHandler)
                .transport(TcpServerTransport.create(port))
                .start()
                .awaitFirstOrNull()
                ?.onClose()
                ?: logger.error("Error starting RSocket server")
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    private fun receiveHandler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.just(RSocketReceiveHandler())
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
