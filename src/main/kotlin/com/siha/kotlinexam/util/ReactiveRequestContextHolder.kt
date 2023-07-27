package com.siha.kotlinexam.util

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono
import reactor.util.context.ContextView


object ReactiveRequestContextHolder {
    val CONTEXT_KEY: Class<ServerHttpRequest> = ServerHttpRequest::class.java

    suspend fun getRequest(): ServerHttpRequest? {
        return Mono.deferContextual { ctx: ContextView -> Mono.just(ctx.get(CONTEXT_KEY)) }.awaitSingleOrNull()
    }
}