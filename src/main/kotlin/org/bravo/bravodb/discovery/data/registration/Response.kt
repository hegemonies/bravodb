package org.bravo.bravodb.discovery.data.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.InstanceInfo

data class Response(
    val status: Answer,
    val otherInstances: List<InstanceInfo>?
) {
    companion object {
        fun fromJson(json: String): Response = jacksonObjectMapper().readValue(json)
    }
}
