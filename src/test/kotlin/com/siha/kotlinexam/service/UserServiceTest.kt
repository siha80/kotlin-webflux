package com.siha.kotlinexam.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
class UserServiceTest : FunSpec({
    test("let") {
        val str = "test"

        val let = str.let { "$it<---" }
        let shouldBe "$str<---"

        str.also { println("ALSO: $it") }
        val applied = str.apply {

        }
        println("APPLIED: $applied")
    }

    test("coroutine async test") {
        val result1: Deferred<String> = async {
            delay(1200)
            println("deferred 1")
            "result1 1 sec delayed"
        }
        val result2: Deferred<String> = async {
            delay(1000)
            println("deferred 2")
            "result2 1 sec delayed"
        }

        val (join, elapsed) = measureTimedValue{
            awaitAll(result1, result2)
        }

        println("RESULT: $join, elapsed: $elapsed")
    }

    test("coroutine launch test") {
        val poolContext = newSingleThreadContext("test-launch-context")

        val result: Job = launch(poolContext){
        }
        result.join()
    }

})
