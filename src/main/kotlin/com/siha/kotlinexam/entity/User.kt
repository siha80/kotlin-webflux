package com.siha.kotlinexam.entity

import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

data class User(
        val id: Int,
        val name: String,
        val dateOfBirth: String
)

@Repository
class UserRepository {
    private val userList = listOf(
            User(1, "test user1", "1990-01-01"),
            User(2, "test user2", "1999-08-01"),
            User(3, "test user3", "1993-01-01"),
            User(4, "test user4", "1997-03-01"),
            User(5, "test user5", "1995-01-01"),
    )

    fun findAll(): Flux<User> = userList.toFlux()

    fun findById(id: Int): User? = userList.find { it.id == id }

}