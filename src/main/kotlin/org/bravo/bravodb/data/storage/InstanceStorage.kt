package org.bravo.bravodb.data.storage

import org.bravo.bravodb.data.storage.model.InstanceInfo
import java.util.concurrent.ConcurrentLinkedQueue

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

    suspend fun save(host: String, port: Int) = save(InstanceInfo(host, port))

    private fun instanceExists(instance: InstanceInfo): Boolean {
        instances.find {
            it == instance
        }.let {
            return when (it) {
                null -> false
                else -> true
            }
        }
    }

    suspend fun findAll() = instances

    suspend fun delete(instance: InstanceInfo) = instances.remove(instance)
}
