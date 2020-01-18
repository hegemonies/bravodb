package org.bravo.bravodb.discovery.server

import org.bravo.bravodb.discovery.config.ServerConfig
import java.util.logging.Logger

/**
 * Discovery server
 */
class Server {

    fun start() {
        logger.info("Hello")
    }

    companion object {
        private val logger = Logger.getLogger(this::class.java.declaringClass.name)
    }
}
