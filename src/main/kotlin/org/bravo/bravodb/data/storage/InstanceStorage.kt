package org.bravo.bravodb.data.storage

import org.apache.logging.log4j.LogManager
import org.bravo.bravodb.data.storage.model.InstanceInfo
import java.util.concurrent.ConcurrentLinkedQueue

private val logger = LogManager.getLogger()

/**
 * Storage all information about database instances in network
 */
object InstanceStorage {

    private val instances = ConcurrentLinkedQueue<InstanceInfo>()

    suspend fun save(instance: InstanceInfo) =
        if (!instanceExists(instance)) {
            instances.add(instance)
        } else {
            true
        }

    suspend fun save(host: String, port: Int) =
        save(InstanceInfo(host, port)).also {
            logger.info("Save instance: $host:$port")
        }

    private suspend fun instanceExists(instance: InstanceInfo): Boolean {
        instances.find {
            it.host == instance.host && it.port == instance.port
        }.let {
            return when (it) {
                null -> false
                else -> true
            }
        }
    }

    fun findAll() = instances

    fun findByHost(host: String) =
        instances.filter {
            it.host == host
        }

    fun findByPort(port: Int) =
        instances.filter {
            it.port == port
        }

    fun findByHostAndPort(host: String, port: Int) =
        instances.find {
            it.host == host && it.port == port
        }

    fun delete(instance: InstanceInfo) = instances.remove(instance)
}
