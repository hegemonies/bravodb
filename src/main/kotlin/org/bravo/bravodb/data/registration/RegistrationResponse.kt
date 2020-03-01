package org.bravo.bravodb.data.registration

import org.bravo.bravodb.data.common.JsonConverter
import org.bravo.bravodb.data.storage.model.InstanceInfo
import java.util.*

data class RegistrationResponse(
    val otherInstances: Queue<InstanceInfo>?
) : JsonConverter()
