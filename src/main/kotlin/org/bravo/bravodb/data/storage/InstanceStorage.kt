package org.bravo.bravodb.data.storage

import org.bravo.bravodb.data.transport.InstanceInfo
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Storage all information about database instances in network
 */
object InstanceStorage : Storage {

    override val instances = ConcurrentLinkedQueue<InstanceInfo>()

    override suspend fun save(instance: InstanceInfo) =
        if (!instanceExists(instance)) {
            instances.add(instance)
        } else {
            true
        }

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

    override suspend fun findAll() =
        instances

    override suspend fun delete(instance: InstanceInfo) =
        instances.remove(instance)
}
