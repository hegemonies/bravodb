package org.bravo.bravodb.data.storage

import org.bravo.bravodb.data.storage.model.DataUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Contain create and delete function on [DataUnit] storage
 */
object DataStorage {
    // private val pool = ConcurrentLinkedQueue<DataUnit>()
    val pool = ConcurrentHashMap<String, String>()

    suspend fun save(key: String, value: String) = this.save(DataUnit(key, value))

    suspend fun save(unit: DataUnit) {
        pool[unit.key] = unit.value
    }

    suspend fun findAll() = pool

    suspend fun delete(unit: DataUnit): Boolean = pool.remove(unit.key, unit.value)

    fun findByKey(key: String) =
        pool[key]?.let { value ->
            DataUnit(key, value)
        }

    suspend fun deleteAll() =
        pool.forEach {
            delete(DataUnit(it.key, it.value))
        }
}
