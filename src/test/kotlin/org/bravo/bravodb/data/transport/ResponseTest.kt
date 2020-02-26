package org.bravo.bravodb.data.transport

import org.bravo.bravodb.data.common.fromJson
import org.junit.jupiter.api.Test

internal class ResponseTest {

    @Test
    fun `simple test`() {
        val responseJson = Response(Answer(AnswerStatus.OK, ""), DataType.ADD_DATA_REQUEST, "").toJson()
        println(responseJson)
        val responseOrigin = fromJson<Response>(responseJson)
        println(responseOrigin)
    }
}
