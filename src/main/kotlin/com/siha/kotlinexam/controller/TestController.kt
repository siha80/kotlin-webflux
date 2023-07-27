package com.siha.kotlinexam.controller

import com.siha.kotlinexam.config.toResponseEntity
import com.siha.kotlinexam.domain.ResponseT
import com.siha.kotlinexam.entity.User
import com.siha.kotlinexam.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(private val userService: UserService) {
    @GetMapping("/v1/user/list")
    suspend fun list(): ResponseEntity<ResponseT<List<User>>> {
        return ResponseT(userService.getList()).toResponseEntity()
    }
}