package org.bravo.bravodb.discovery.server.transport

import kotlinx.coroutines.flow.Flow

data class Data(
        val data: Flow<ByteArray>
)
