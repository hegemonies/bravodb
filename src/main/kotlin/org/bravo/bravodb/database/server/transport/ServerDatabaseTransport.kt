package org.bravo.bravodb.database.server.transport

interface ServerDatabaseTransport {
    val port: Int
    val host: String
    suspend fun start()
}
