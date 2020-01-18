package org.bravo.bravodb.discovery.server

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.config.ServerConfig

/**
 * Discovery server
 */
class Server {

    fun start(config: ServerConfig) {
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
