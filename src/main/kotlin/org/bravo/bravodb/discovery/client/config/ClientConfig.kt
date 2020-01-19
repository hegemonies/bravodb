package org.bravo.bravodb.discovery.client.config

import org.bravo.bravodb.discovery.client.transport.ClientTransport
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient

/**
 * Contain client config data and building
 */
class ClientConfig private constructor(
    val port: Int,
    val host: String,
    val transport: ClientTransport
) {

    data class Builder(
        var port: Int = 8919,
        var host: String = "localhost",
        var transport: ClientTransport = RSocketClient()
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(transport: ClientTransport) = apply {
            transport.port = port
            transport.host = host
            this.transport = transport
        }

        fun build() = ClientConfig(port, host, transport)
    }
}
