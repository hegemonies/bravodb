package org.bravo.bravodb.database.server.config

import org.bravo.bravodb.database.consts.DefaultDatabaseInfo
import org.bravo.bravodb.database.server.transport.ServerDatabaseTransport
import org.bravo.bravodb.database.server.transport.rsocket.RSocketServerDatabaseTransport

class ServerDatabaseConfig private constructor(
    val port: Int,
    val host: String,
    val transport: ServerDatabaseTransport
) {

    data class Builder(
        var port: Int = DefaultDatabaseInfo.PORT,
        var host: String = DefaultDatabaseInfo.HOST,
        var transport: Class<in ServerDatabaseTransport> = RSocketServerDatabaseTransport.javaClass
    ) {
        fun setPort(port: Int) = apply { this.port = port }
        fun setHost(host: String) = apply { this.host = host }
        fun setTransport(transport: Class<in ServerDatabaseTransport>) = apply {
            this.transport = transport
        }

        fun build(): ServerDatabaseConfig =
            ServerDatabaseConfig(
                port,
                host,
                transport.declaringClass.getConstructor().newInstance() as ServerDatabaseTransport
            )
    }
}
