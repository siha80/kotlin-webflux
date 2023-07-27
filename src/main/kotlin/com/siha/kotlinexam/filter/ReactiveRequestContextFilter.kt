package com.siha.kotlinexam.filter

import com.siha.kotlinexam.util.ReactiveRequestContextHolder
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context


@Configuration
class ReactiveRequestContextFilter: WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request: ServerHttpRequest = exchange.request
        return chain.filter(exchange)
                .contextWrite(Context.of(ReactiveRequestContextHolder.CONTEXT_KEY, request))
    }
}