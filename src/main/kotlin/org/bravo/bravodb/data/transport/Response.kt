package org.bravo.bravodb.data.transport

import org.bravo.bravodb.data.common.JsonConverter

data class Response(
    val answer: Answer,
    val type: DataType,
    val body: String
) : JsonConverter()
