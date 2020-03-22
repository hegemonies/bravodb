package org.bravo.bravodb.client.transport

import org.bravo.bravodb.data.storage.model.DataUnit

interface Client {
    val host: String
    val port: Int

    suspend fun connect(): Boolean
    suspend fun registration(selfHost: String, selfPort: Int): Boolean
    suspend fun sendData(unit: DataUnit)
}
