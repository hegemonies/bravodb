package org.bravo.bravodb.discovery.client.transport

interface ClientTransport {
    var port: Int
    var host: String
    suspend fun selfRegistration()
}
