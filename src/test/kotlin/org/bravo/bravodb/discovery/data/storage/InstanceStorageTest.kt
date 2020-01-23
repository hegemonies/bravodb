package org.bravo.bravodb.discovery.data.storage

import kotlinx.coroutines.runBlocking
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InstanceStorageTest {

    private val instanceOne = InstanceInfo("1", 1)
    private val instanceTwo = InstanceInfo("2", 2)
    private val instanceThree = InstanceInfo("3", 3)

    @Test
    fun savingTest() {
        runBlocking {
            InstanceStorage.save(instanceOne)
            InstanceStorage.save(instanceThree)

            InstanceStorage.instances.forEach {
                if (it.port == 1) {
                    InstanceStorage.save(instanceTwo)
                }
            }

            assertTrue(InstanceStorage.instances.contains(instanceOne))
            assertTrue(InstanceStorage.instances.contains(instanceTwo))
            assertTrue(InstanceStorage.instances.contains(instanceThree))
        }
    }
}
