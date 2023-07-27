package com.siha.kotlinexam.service

import arrow.core.Either
import com.siha.kotlinexam.entity.User
import com.siha.kotlinexam.entity.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun getList(): List<User> =
            userRepository.findAll()
                    .asFlow()
                    .map { u ->
                        u
                    }
                    .toList()

    fun testResult() {
        val result = Either.Right("test")
        val mapped = result.fold(
                { l -> "LEFT: $l" },
                { r -> "RIGHT: $r" }
        )

        logger.info("MAPPED: $mapped")
    }
}