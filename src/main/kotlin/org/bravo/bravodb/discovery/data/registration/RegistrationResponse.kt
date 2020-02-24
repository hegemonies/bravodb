package org.bravo.bravodb.discovery.data.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import java.util.*

data class RegistrationResponse(
    val otherInstances: Queue<InstanceInfo>?
) {

    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun fromJson(json: String): RegistrationResponse = jacksonObjectMapper().readValue(json)
    }
}
