package org.bravo.bravodb.discovery.client

interface ClientTransport {
    suspend fun selfRegistration()
}
