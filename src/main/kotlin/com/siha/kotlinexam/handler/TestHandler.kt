package com.siha.kotlinexam.handler

import com.siha.kotlinexam.service.UserService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class TestHandler(private val userService: UserService) {
    suspend fun list(serverRequest: ServerRequest): ServerResponse {
        val userList = userService.getList()
        return ServerResponse.ok().bodyValueAndAwait(userList)
    }
}