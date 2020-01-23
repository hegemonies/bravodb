package org.bravo.bravodb.discovery.data.storage

import org.bravo.bravodb.discovery.data.common.InstanceInfo
import java.util.*

interface Storage {
    val instances: Queue<InstanceInfo>

    suspend fun save(instance: InstanceInfo): Boolean
    suspend fun findAll(): Queue<InstanceInfo>
    suspend fun delete(instance: InstanceInfo): Boolean
}
