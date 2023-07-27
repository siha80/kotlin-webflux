package com.siha.kotlinexam.config

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.siha.kotlinexam.annotation.DisableLog
import com.siha.kotlinexam.util.ReactiveRequestContextHolder
import mu.KotlinLogging
import net.logstash.logback.encoder.org.apache.commons.lang3.ClassUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.annotation.Configuration
import org.springframework.core.KotlinDetector
import org.springframework.web.reactive.function.server.*
import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.*

@Configuration
@Aspect
class LoggingConfig {
    private val objectMapper = jacksonObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(ParameterNamesModule())

    private val toStringConditions: List<(Any) -> Boolean> = listOf(
            { v -> ClassUtils.isPrimitiveOrWrapper(v.javaClass) },
            { v -> ClassUtils.isAssignable(v.javaClass, Throwable::class.java) }
    )

    private val logger = KotlinLogging.logger { }

    suspend fun ProceedingJoinPoint.proceedCoroutine(args: Array<Any?>? = null): Any? =
            suspendCoroutineUninterceptedOrReturn { continuation ->
                val argsArray = args ?: this.args.sliceArray(0 until this.args.size - 1)
                this.proceed(argsArray + continuation)
            }

    suspend fun ProceedingJoinPoint.proceedCoroutineFn(args: Any? = null): Any? =
            suspendCoroutineUninterceptedOrReturn { continuation ->
                val argsArray = args?.let { arrayOf(args) } ?: this.args.sliceArray(0 until this.args.size - 1)
                this.proceed(argsArray + continuation)
            }

    fun ProceedingJoinPoint.runCoroutine(block: suspend () -> Any?): Any? =
            block.startCoroutineUninterceptedOrReturn(this.args.last() as Continuation<Any?>)

    @Pointcut(
            "within(com.siha.kotlinexam..*) " +
                    "&& (" +
                    " within(@org.springframework.web.bind.annotation.RestController *) " +
                    ")"
    )
    fun restApis() {
    }

    @Pointcut(
            "within(com.siha.kotlinexam.handler..*) " +
                    "&& (" +
                    " within(@org.springframework.stereotype.Service *) " +
                    ")"
    )
    fun handlers() {
    }

    @Pointcut(
            ("within(com.siha.kotlinexam..*) " +
                    "&& (" +
                    "within(@org.springframework.stereotype.Service *)" +
                    " || within(@org.springframework.stereotype.Component *)" +
                    ")" +
                    "&& (" +
                    "!within(com.siha.kotlinexam.handler..*)" +
                    ")")
    )
    fun services() {
    }

    @Around(value = "restApis()")
    fun loggingApi(jp: ProceedingJoinPoint): Any? {
        return jp.runCoroutine {
            val uri = ReactiveRequestContextHolder.getRequest()?.uri?.path
                    ?: "NOT-HTTP-REQUEST"
            logAfterProceedCoroutine(jp, "API", uri)
        }
    }

    @Around(value = "handlers()")
    fun loggingHandler(jp: ProceedingJoinPoint): Any? {
        return jp.runCoroutine {
            val uri = ReactiveRequestContextHolder.getRequest()?.uri?.path
                    ?: "NOT-HTTP-REQUEST"
            logAfterProceedCoroutineFn(jp, "API", uri)
        }
    }

    @Around(value = "services()")
    fun loggingService(jp: ProceedingJoinPoint): Any? {
        val signature = jp.signature as MethodSignature
        val isSuspend = KotlinDetector.isSuspendingFunction(signature.method)

        return if (isSuspend) {
            jp.runCoroutine {
                logAfterProceedCoroutine(jp, "SERVICE")
            }
        } else {
            logAfterProceed(jp)
        }
    }

    private suspend fun logAfterProceedCoroutineFn(jp: ProceedingJoinPoint, typeName: String, uri: String? = null): Any? {
        val request = jp.args.first() as ServerRequest
        val method = request.path()
        val body = request.awaitBodyOrNull<String>()

        return (jp.proceedCoroutineFn(body?.let {
            ServerRequest.from(request).body(body).build()
        } ?: request) as ServerResponse)
                .also {
                    val params = buildString {
                        if (request.queryParams().isNotEmpty()) {
                            append(objectMapper.writeValueAsString(request.queryParams()))
                        }
                        body?.let { append("${objectMapper.readTree(body)}") }
                    }

                    val returns = if (it is EntityResponse<*>) it.entity() else it
                    printHandlerLog((jp.signature as MethodSignature).method, jp.signature.toShortString(), params, returns)
                }
    }

    private suspend fun logAfterProceedCoroutine(jp: ProceedingJoinPoint, typeName: String, uri: String? = null): Any? {
        val args = jp.args.sliceArray(0 until jp.args.size - 1)
        val methodName = jp.signature.toShortString()
        val argString = if (args.isNotEmpty()) args.joinToString(", ", transform = ::mapToArgument) else "NONE"

        return jp.proceedCoroutine(args)
                .also {
                    printLog((jp.signature as MethodSignature).method, typeName, methodName, argString, it, uri)
                }
    }

    private fun logAfterProceed(jp: ProceedingJoinPoint, typeName: String = "SERVICE"): Any? {
        val methodName = jp.signature.toShortString()
        val argString = if (jp.args.isNotEmpty()) jp.args.joinToString(", ", transform = ::mapToArgument) else "NONE"

        return jp.proceed(jp.args)
                .also {
                    printLog((jp.signature as MethodSignature).method, typeName, methodName, argString, it)
                }
    }

    private fun printLog(method: Method, typeName: String, methodName: String, args: String, ret: Any?, uri: String? = null) {
        if (method.getAnnotation(DisableLog::class.java) == null) {
            val retString: String = ret?.let(::mapToArgument) ?: "VOID"
            logger.info("[CALLED - $typeName]${uri?.let { "[URI: $it]" } ?: ""} $methodName [REQUEST] : $args | [RETURN] : $retString")
        }
    }

    private fun printHandlerLog(method: Method, methodName: String, args: String, ret: Any?, uri: String? = null) {
        if (method.getAnnotation(DisableLog::class.java) == null) {
            val retString: String = ret?.let(::mapToArgument) ?: "VOID"
            logger.info("[CALLED - HANDLER]${uri?.let { "[URI: $it]" } ?: ""} $methodName [REQUEST] : $args | [RETURN] : $retString")
        }
    }

    private fun mapToArgument(value: Any): String {
        val shouldApplyToString: Boolean = value
                .let { v -> toStringConditions.all { it(v) } }

        return if (shouldApplyToString) {
            value.toString()
        } else {
            val returnValue = if (value is EntityResponse<*>) {
                value.entity()
            } else value

            if (value !is ServerRequest) {
                "${returnValue.javaClass.simpleName}:${objectMapper.writeValueAsString(returnValue)}"
            } else "REQUEST"
        }
    }
}