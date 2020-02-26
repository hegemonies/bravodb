package org.bravo.bravodb.data.database

import org.bravo.bravodb.data.common.JsonConverter

data class AddDataRequest(
    val key: String,
    val value: String
) : JsonConverter()
