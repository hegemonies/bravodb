package org.bravo.bravodb.discovery.data.common

data class Answer(
    val statusCode: AnswerStatus,
    val message: String? = null
)
