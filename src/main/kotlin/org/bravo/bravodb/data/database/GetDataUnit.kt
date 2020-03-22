package org.bravo.bravodb.data.database

import org.bravo.bravodb.data.common.JsonConverter

// Request
data class GetDataUnit(
    val key: String
) : JsonConverter()

// Response is DataUnit
