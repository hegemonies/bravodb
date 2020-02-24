package org.bravo.bravodb.discovery.data.common

import org.bravo.bravodb.discovery.data.registration.RegistrationRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RequestTest {

    @Test
    fun `simple test`() {
        val data = RegistrationRequest(InstanceInfo("localhost", 7777)).toJson()

        val requestJson = Request(
            DataType.REGISTRATION_REQUEST,
            data
        ).also {
            println(it)
        }.toJson().also {
            println(it)
        }

        val request = Request.fromJson(requestJson).also {
            println(it)
        }

        when(request.type) {
            DataType.REGISTRATION_REQUEST -> println(RegistrationRequest.fromJson(request.body))
            else -> Assertions.fail()
        }
    }
}