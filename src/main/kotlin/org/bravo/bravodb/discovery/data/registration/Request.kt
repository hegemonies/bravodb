package org.bravo.bravodb.discovery.data.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bravo.bravodb.discovery.data.common.InstanceInfo

data class Request(
    val instanceInfo: InstanceInfo
) {
    fun toJson(): String = jacksonObjectMapper().writeValueAsString(this)
}
