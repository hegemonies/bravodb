package org.bravo.bravodb.data.registration

import org.bravo.bravodb.data.common.JsonConverter
import org.bravo.bravodb.data.transport.InstanceInfo

data class RegistrationRequest(
    val instanceInfo: InstanceInfo
) : JsonConverter()
