package com.mbitsystem.sliide.usersmanager

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object ErrorResponseMock {

    fun createHttpError(erroCode: Int) =
        HttpException(
            Response.error<Int>(
                erroCode,
                """
                    {
                  "statusCode": $erroCode
                }
            """.trimIndent().toResponseBody()
            )
        )
}
