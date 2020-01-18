package org.bravo.bravodb.discovery.server.config

import org.bravo.bravodb.discovery.server.transport.ServerTransport

data class ServerConfig(
        val transport: ServerTransport,
        val port: Int
)
