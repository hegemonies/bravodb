package org.bravo.bravodb.discovery.data.storage

import org.bravo.bravodb.discovery.data.common.InstanceInfo

interface Storage {
    val instances: List<InstanceInfo>

    suspend fun save(instance: InstanceInfo): Boolean
    suspend fun findAll(): List<InstanceInfo>
    suspend fun delete(instance: InstanceInfo): Boolean
}
