package util

import org.http4k.core.Status

sealed class Result {
    class DataResult<T>(val data: T) : Result() {
        override fun toString() = data.toString()
    }

    class ErrorResult(val status: Status, val response: String) : Result() {
        override fun toString() = """
            {
                Status: $status
                Response: {
                    ${response.prependIndent("        ")}
                }
            }
        """.trimIndent()
    }

    class ExceptionResult(val exception: Exception, val response: String) : Result() {
        override fun toString() = """
            {
                Exception: {
                ${exception.toString().prependIndent("        ")}
                }
                Response: {
                ${response.prependIndent("        ")}
                }
            }
        """.trimIndent()
    }

    inline fun <reified R> resolve(): R = when (this) {
        is ErrorResult -> throw Exception("An error has happened: $this")
        is ExceptionResult -> throw Exception("An exception has happened: $this", exception)
        is DataResult<*> -> data as? R ?: throw Exception("Invalid response from the server: $this")
    }
}