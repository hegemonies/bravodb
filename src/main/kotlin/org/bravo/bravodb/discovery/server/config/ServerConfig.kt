package org.bravo.bravodb.discovery.server.config

import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import org.bravo.bravodb.discovery.server.transport.ServerTransport
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServer

class ServerConfig private constructor(
    val port: Int,
    val host: String,
    val transport: ServerTransport
) {

    data class Builder(
        var port: Int = DefaultConnectInfo.PORT,
        var host: String = DefaultConnectInfo.HOST,
        var transport: ServerTransport = RSocketServer()
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(transport: ServerTransport) = apply {
            transport.port = port
            transport.host = host
            this.transport = transport
        }

        fun build() = ServerConfig(port, host, transport)
    }
}
