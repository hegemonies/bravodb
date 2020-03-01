package org.bravo.bravodb.data.registration

import org.bravo.bravodb.data.common.JsonConverter
import org.bravo.bravodb.data.storage.model.InstanceInfo

data class RegistrationRequest(
    val instanceInfo: InstanceInfo
) : JsonConverter()
