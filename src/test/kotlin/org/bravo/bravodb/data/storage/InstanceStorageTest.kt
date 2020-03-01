package org.bravo.bravodb.data.storage

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bravo.bravodb.data.storage.model.InstanceInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InstanceStorageTest {

    private val storage = InstanceStorage

    private val instanceOne = InstanceInfo("1", 1)
    private val instanceTwo = InstanceInfo("2", 2)
    private val instanceThree = InstanceInfo("3", 3)

    @BeforeEach
    fun cleanStorage() {
        runBlocking {
            storage.pool.forEach {
                storage.delete(it)
            }
        }
    }

    @Test
    fun savingTest() {
        runBlocking {
            storage.save(instanceOne)
            storage.save(instanceThree)

            storage.pool.forEach {
                if (it.port == 1) {
                    storage.save(instanceTwo)
                }
            }

            assertTrue(storage.pool.contains(instanceOne))
            assertTrue(storage.pool.contains(instanceTwo))
            assertTrue(storage.pool.contains(instanceThree))
        }
    }

    @Test
    fun multithreadTest() {
        val countRecords = 200

        val deferred = GlobalScope.async {
            (0 until (countRecords / 2) - 1).forEach {
                storage.save(InstanceInfo("$it", it))
            }
        }

        runBlocking {
            ((countRecords / 2)..countRecords).forEach {
                storage.save(InstanceInfo("$it", it))
            }

            deferred.await()
        }

        assertEquals(storage.pool.size, countRecords)
    }
}
