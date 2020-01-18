package org.bravo.bravodb.discovery.client.config

import org.bravo.bravodb.discovery.client.ClientTransport
import org.bravo.bravodb.discovery.client.rsocket.RSocketClient

/**
 * Contain client config data and building
 */
class ClientConfig private constructor(
        private val port: Int
) {

    companion object {
        fun port(port: Int) = ClientConfig(port)
    }

    fun rsocketTransport(): ClientTransport {
        return RSocketClient(port)
    }
}
