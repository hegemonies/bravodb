package org.bravo.bravodb.discovery.server.transport

interface ServerDiscoveryTransport {
    var port: Int
    var host: String
    suspend fun start()
}
