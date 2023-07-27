package com.siha.kotlinexam.route

import com.siha.kotlinexam.handler.TestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TestRouter(private val testHandler: TestHandler) {

    @Bean
    fun route()  = coRouter {
        GET("/v1/list", testHandler::list)
    }
}