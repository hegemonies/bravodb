package org.bravo.bravodb.data.storage.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bravo.bravodb.discovery.client.Client
import org.bravo.bravodb.discovery.client.config.ClientConfig

data class InstanceInfo(
    val host: String,
    val port: Int,
    @JsonIgnore val client: Client = Client(ClientConfig.Builder(port, host).build())
)
