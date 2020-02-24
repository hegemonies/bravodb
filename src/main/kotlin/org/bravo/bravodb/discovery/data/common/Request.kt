package org.bravo.bravodb.discovery.data.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class Request(
    val type: DataType,
    val body: String
) {

    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = jacksonObjectMapper().readValue<Request>(json)
    }
}
