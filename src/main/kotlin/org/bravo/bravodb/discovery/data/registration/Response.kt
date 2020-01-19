package org.bravo.bravodb.discovery.data.registration

import org.bravo.bravodb.discovery.data.common.Answer
import org.bravo.bravodb.discovery.data.common.InstanceInfo

data class Response(
    val status: Answer,
    val otherInstances: List<InstanceInfo>?
)
