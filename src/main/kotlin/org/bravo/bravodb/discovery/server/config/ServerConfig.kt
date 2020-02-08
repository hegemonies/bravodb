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
        var transport: Class<in ServerTransport> = RSocketServer.javaClass
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(transport: Class<in ServerTransport>) = apply {
            this.transport = transport
        }

        fun build(): ServerConfig =
            ServerConfig(
                port,
                host,
                transport.declaringClass.getConstructor().newInstance() as ServerTransport
            )
    }
}
