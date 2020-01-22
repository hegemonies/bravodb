package org.bravo.bravodb.discovery.client

import org.bravo.bravodb.discovery.client.config.ClientConfig
import org.bravo.bravodb.discovery.client.transport.ClientTransport
import org.bravo.bravodb.discovery.data.common.InstanceInfo

class Client(
    config: ClientConfig
) {
    private val transport: ClientTransport = config.transport

    suspend fun registrationIn(instanceInfo: InstanceInfo) = transport.registrationIn(instanceInfo)
}
