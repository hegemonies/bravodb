package org.bravo.bravodb.discovery.server.config

import org.bravo.bravodb.database.server.transport.rsocket.RSocketServerDatabaseTransport
import org.bravo.bravodb.discovery.consts.DefaultDiscoveryConnectInfo
import org.bravo.bravodb.discovery.server.transport.ServerDiscoveryTransport
import org.bravo.bravodb.discovery.server.transport.rsocket.RSocketServerDiscovery

class ServerDiscoveryConfig private constructor(
    val port: Int,
    val host: String,
    val discoveryTransport: ServerDiscoveryTransport
) {

    data class Builder(
        var port: Int = DefaultDiscoveryConnectInfo.PORT,
        var host: String = DefaultDiscoveryConnectInfo.HOST,
        var discoveryTransport: Class<in ServerDiscoveryTransport> = RSocketServerDiscovery.javaClass
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(discoveryTransport: Class<in ServerDiscoveryTransport>) = apply {
            this.discoveryTransport = discoveryTransport
        }

        fun build(): ServerDiscoveryConfig =
            ServerDiscoveryConfig(
                port,
                host,
                discoveryTransport.declaringClass.getConstructor().newInstance() as ServerDiscoveryTransport
            )
    }
}
