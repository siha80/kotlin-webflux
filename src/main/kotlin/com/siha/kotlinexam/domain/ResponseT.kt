package com.siha.kotlinexam.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class ReturnCode {
    SUCCESS,
    UNKNOWN,
}

@Serializable
data class ResponseT<out T: Any>(
        @Transient val status: ReturnCode? = null,
        val returnCode: String,
        val returnMessage: String? = null,
        val data: T? = null
) {
    constructor(status: ReturnCode): this(status, status.name);
    constructor(status: ReturnCode, data: T): this(status = status, returnCode = status.name, data = data)
    constructor(status: ReturnCode, message: String, data: T): this(status, status.name, message, data)
    constructor(data: T): this(ReturnCode.SUCCESS, data)
    fun success(): Boolean = status
            ?.let { it == ReturnCode.SUCCESS }
            ?: (returnCode == ReturnCode.SUCCESS.name)
}