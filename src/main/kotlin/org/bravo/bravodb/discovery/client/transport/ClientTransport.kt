package org.bravo.bravodb.discovery.client.transport

interface ClientTransport {
    suspend fun registration(host: String, port: Int): Boolean
}
