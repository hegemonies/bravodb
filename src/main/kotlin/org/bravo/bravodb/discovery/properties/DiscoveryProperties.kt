package org.bravo.bravodb.discovery.properties

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.consts.DefaultDiscoveryConnectInfo
import java.util.*
import kotlin.math.log
import kotlin.system.exitProcess

/**
 * Contain discovery service properties
 * [selfClientHost]: "bravodb.discovery.client.self.host"
 * [selfClientPort]: "bravodb.discovery.client.self.port"
 * [selfServerHost]: "bravodb.discovery.server.self.host"
 * [selfServerPort]: "bravodb.discovery.server.self.port"
 * [otherServerHost]: "bravodb.discovery.server.other.host"
 * [otherServerPort]: "bravodb.discovery.server.other.port"
 */
data class DiscoveryProperties private constructor(
    val selfServerHost: String,
    val selfServerPort: Int,

    val otherServerHost: String?,
    val otherServerPort: Int?
) {

    companion object {
        /**
         * Build [DiscoveryProperties]
         * Reading properties file from classpath.
         * If no find properties, use default values from [DefaultDiscoveryConnectInfo]
         * @return [DiscoveryProperties] after reading properties file.
         * Can return null.
         */
        fun fromResourceFile(resourceFilename: String): DiscoveryProperties? {
            this::class.java.classLoader.getResourceAsStream(resourceFilename).use {
                val properties = Properties()
                try {
                    properties.load(it)
                } catch (e: Exception) {
                    logger.error("Can not read properties file $resourceFilename", e)
                    return null
                }

                val selfServerHost = properties.getProperty(
                    "bravodb.discovery.server.self.host",
                    DefaultDiscoveryConnectInfo.HOST
                )

                val selfServerPort = properties.getProperty(
                    "bravodb.discovery.server.self.port",
                    DefaultDiscoveryConnectInfo.PORT.toString()
                ).toInt()

                val otherServerHost = properties.getProperty(
                    "bravodb.discovery.server.other.host",
                    DefaultDiscoveryConnectInfo.OTHER_SERVER_HOST
                )

                val otherServerPort = properties.getProperty(
                    "bravodb.discovery.server.other.port",
                    DefaultDiscoveryConnectInfo.OTHER_SERVER_PORT.toString()
                ).toInt()

                return DiscoveryProperties(
                    selfServerHost, selfServerPort,
                    otherServerHost, otherServerPort
                )
            }
        }

        fun fromEnvironments(): DiscoveryProperties {
            val selfHost = System.getenv("bravodb.discovery.server.self.host")
                ?: errorLog("Self host must be set")
            val selfPort = System.getenv("bravodb.discovery.server.self.port")?.toInt()
                ?: errorLog("Self port must be set")

            val otherHost = System.getenv("bravodb.discovery.server.other.host")
            otherHost ?: logger.warn("Host other instance does not set")
            val otherPort = System.getenv("bravodb.discovery.server.other.port")?.toInt()
            otherPort ?: logger.warn("Port other instance does not set")

            return DiscoveryProperties(selfHost, selfPort, otherHost, otherPort)
        }

        private fun errorLog(message: String): Nothing {
            logger.error(message)
            exitProcess(1)
        }

        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
