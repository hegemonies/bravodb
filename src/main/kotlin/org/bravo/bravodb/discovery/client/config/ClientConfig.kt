package org.bravo.bravodb.discovery.client.config

import org.bravo.bravodb.discovery.client.transport.ClientTransport
import org.bravo.bravodb.discovery.client.transport.rsocket.RSocketClient
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo

/**
 * Contain client config data and building
 */
class ClientConfig private constructor(
    val port: Int,
    val host: String,
    val transport: ClientTransport
) {

    data class Builder(
        var port: Int = DefaultConnectInfo.PORT,
        var host: String = DefaultConnectInfo.HOST,
        var transport: Class<in ClientTransport> = RSocketClient.javaClass
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(transport: Class<in ClientTransport>) = apply {
            this.transport = transport
        }

        fun build() =
            ClientConfig(
                port,
                host,
                transport.declaringClass.getConstructor().newInstance() as ClientTransport
            )
    }
}
