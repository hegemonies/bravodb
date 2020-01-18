package org.bravo.bravodb.discovery.transport

interface Transport {
    fun receive(): Data
    fun publish(data: Data)
}
