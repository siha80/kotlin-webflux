package com.siha.kotlinexam.config

import com.siha.kotlinexam.domain.ResponseT
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun ResponseT<*>.getHttpStatus(successStatus: HttpStatus): HttpStatus {
    return if(this.success()) successStatus else HttpStatus.BAD_REQUEST
}

fun ResponseT<*>.getHttpStatus(): HttpStatus {
    return this.getHttpStatus(HttpStatus.OK)
}

fun <T: Any> ResponseT<T>.toResponseEntity(): ResponseEntity<ResponseT<T>> {
    return this.toResponseEntity(HttpStatus.OK)
}

fun <T: Any> ResponseT<T>.toResponseEntity(successStatus: HttpStatus): ResponseEntity<ResponseT<T>> {
    return ResponseEntity(this, this.getHttpStatus(successStatus))
}

