package org.bravo.bravodb.discovery.server.transport

interface ServerTransport {
    var port: Int
    var host: String
    suspend fun start()
}
