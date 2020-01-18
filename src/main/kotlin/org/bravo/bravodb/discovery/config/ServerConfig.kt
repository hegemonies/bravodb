package org.bravo.bravodb.discovery.config

import org.bravo.bravodb.discovery.transport.Transport

data class ServerConfig(
        val transport: Transport,
        val port: String
)
