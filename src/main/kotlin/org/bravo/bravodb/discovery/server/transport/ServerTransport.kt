package org.bravo.bravodb.discovery.server.transport

interface ServerTransport {
    suspend fun start(port: Int)
}
