package com.axiom.aicoach.util

import kotlinx.coroutines.delay

suspend fun <T> withRetry(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 1_000L,
    maxDelayMs: Long = 16_000L,
    factor: Double = 2.0,
    block: suspend () -> T,
): Result<T> {
    var currentDelay = initialDelayMs
    repeat(maxAttempts - 1) { attempt ->
        runCatching { block() }.onSuccess { return Result.success(it) }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
    }
    return runCatching { block() }
}
