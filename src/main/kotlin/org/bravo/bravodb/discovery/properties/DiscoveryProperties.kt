package org.bravo.bravodb.discovery.properties

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.discovery.consts.DefaultConnectInfo
import java.util.*

/**
 * Contain discovery service properties
 * [selfClientHost]: "bravodb.discovery.client.self.host"
 * [selfClientPort]: "bravodb.discovery.client.self.port"
 *
 * [selfServerHost]: "bravodb.discovery.server.self.host"
 * [selfServerPort]: "bravodb.discovery.server.self.port"
 *
 * [otherServerHost]: "bravodb.discovery.server.other.host"
 * [otherServerPort]: "bravodb.discovery.server.other.port"
 */
class DiscoveryProperties private constructor(
    val selfClientHost: String,
    val selfClientPort: Int,

    val selfServerHost: String,
    val selfServerPort: Int,

    val otherServerHost: String,
    val otherServerPort: Int
) {

    companion object {
        /**
         * Build [DiscoveryProperties]
         * If no find properties, use default values from [DefaultConnectInfo]
         * @return [DiscoveryProperties] after reading properties file.
         * Can return null.
         */
        fun fromFile(filename: String): DiscoveryProperties? {
            this::class.java.getResourceAsStream(filename).also {
                val properties = Properties()
                try {
                    properties.load(it)
                } catch (e: Exception) {
                    logger.error("Can not read properties file $filename", e)
                    return null
                }

                val selfClientHost = properties.getProperty(
                    "bravodb.discovery.client.self.host",
                    DefaultConnectInfo.HOST
                )

                val selfClientPort = properties.getProperty(
                    "bravodb.discovery.client.self.port",
                    DefaultConnectInfo.PORT.toString()
                ).toInt()

                val selfServerHost = properties.getProperty(
                    "bravodb.discovery.server.self.host",
                    DefaultConnectInfo.HOST
                )

                val selfServerPort = properties.getProperty(
                    "bravodb.discovery.server.self.port",
                    DefaultConnectInfo.PORT.toString()
                ).toInt()

                val otherServerHost = properties.getProperty(
                    "bravodb.discovery.server.other.host",
                    DefaultConnectInfo.OTHER_SERVER_HOST
                )

                val otherServerPort = properties.getProperty(
                    "bravodb.discovery.server.other.port",
                    DefaultConnectInfo.OTHER_SERVER_PORT.toString()
                ).toInt()

                return DiscoveryProperties(
                    selfClientHost, selfClientPort,
                    selfServerHost, selfServerPort,
                    otherServerHost, otherServerPort
                )
            }
        }

        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}