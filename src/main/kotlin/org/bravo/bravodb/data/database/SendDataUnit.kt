package org.bravo.bravodb.data.database

import org.bravo.bravodb.data.common.JsonConverter
import org.bravo.bravodb.data.transport.Answer

data class SendDataUnit(
    val key: String,
    val value: String
) : JsonConverter()
