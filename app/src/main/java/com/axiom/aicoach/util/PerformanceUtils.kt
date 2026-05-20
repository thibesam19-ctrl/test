package com.axiom.aicoach.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

// Memoize expensive list transformations
@Composable
fun <T, R> rememberMapped(list: List<T>, transform: (T) -> R): State<List<R>> {
    return remember(list) { derivedStateOf { list.map(transform) } }
}

// Stable key for lazy list items
fun Any.stableKey(): String = this::class.simpleName + "@" + hashCode()
