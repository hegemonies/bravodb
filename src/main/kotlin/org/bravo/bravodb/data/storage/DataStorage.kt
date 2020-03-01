package org.bravo.bravodb.data.storage

import org.bravo.bravodb.data.storage.model.DataUnit
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Contain create and delete function on [DataUnit] storage
 */
object DataStorage {
    private val pool = ConcurrentLinkedQueue<DataUnit>()

    suspend fun save(key: String, value: String) = this.save(DataUnit(key, value))

    suspend fun save(unit: DataUnit): Boolean = pool.add(unit)

    suspend fun findAll() = pool

    suspend fun delete(unit: DataUnit): Boolean = pool.remove(unit)
}
