package org.bravo.bravodb.discovery.client.transport

import org.bravo.bravodb.discovery.data.common.InstanceInfo

interface ClientTransport {
    var port: Int
    var host: String
    suspend fun registrationIn(otherInstance: InstanceInfo): Boolean
}
