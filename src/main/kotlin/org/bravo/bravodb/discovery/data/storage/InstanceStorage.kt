package org.bravo.bravodb.discovery.data.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bravo.bravodb.discovery.data.common.InstanceInfo

/**
 * Storage all information about database instances in network
 */
object InstanceStorage: Storage {

    override val instances = mutableListOf<InstanceInfo>()

    private val mutex = Mutex()

    override suspend fun save(instance: InstanceInfo) =
        mutex.withLock {
            if (!instanceExists(instance)) {
                instances.add(instance)
            } else {
                true
            }
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
        mutex.withLock {
            instances
        }

    override suspend fun delete(instance: InstanceInfo) =
        mutex.withLock {
            instances.remove(instance)
        }
}
