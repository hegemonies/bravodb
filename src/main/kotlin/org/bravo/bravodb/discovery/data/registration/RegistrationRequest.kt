package org.bravo.bravodb.discovery.data.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bravo.bravodb.discovery.data.common.InstanceInfo

data class RegistrationRequest(
    val instanceInfo: InstanceInfo
) {

    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun fromJson(json: String): RegistrationRequest = jacksonObjectMapper().readValue(json)
    }
}
