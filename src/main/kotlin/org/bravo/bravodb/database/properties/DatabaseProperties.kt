package org.bravo.bravodb.database.properties

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.database.consts.DefaultDatabaseInfo
import java.util.*

/**
 * Contain database service properties
 * [host]: "bravodb.database.host"
 * [port]: "bravodb.database.port"
 */
class DatabaseProperties private constructor(
    val host: String,
    val port: Int
) {

    companion object {
        /**
         * Build [DatabaseProperties]
         * Reading properties file from classpath.
         * If no find properties, use default values from [DefaultDatabaseInfo]
         * @return [DatabaseProperties] after reading properties file.
         * Can return null.
         */
        fun fromResourceFile(resourceFilename: String): DatabaseProperties? {
            this::class.java.classLoader.getResourceAsStream(resourceFilename).use {
                val properties = Properties()
                try {
                    properties.load(it)
                } catch (e: Exception) {
                    logger.error("Can not read properties file $resourceFilename", e)
                    return null
                }

                val selfClientHost = properties.getProperty(
                    "bravodb.database.host",
                    DefaultDatabaseInfo.HOST
                )

                val selfClientPort = properties.getProperty(
                    "bravodb.database.port",
                    DefaultDatabaseInfo.PORT.toString()
                ).toInt()

                return DatabaseProperties(selfClientHost, selfClientPort)
            }
        }

        private val logger = LogManager.getLogger(this::class.java.declaringClass)
    }
}
