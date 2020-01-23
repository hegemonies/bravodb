package org.bravo.bravodb.discovery.data.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.InstanceInfo
import java.util.*

data class Response(
    val status: Answer,
    val otherInstances: Queue<InstanceInfo>?
) {

    companion object {
        fun fromJson(json: String): Response = jacksonObjectMapper().readValue(json)
    }

    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)
}
