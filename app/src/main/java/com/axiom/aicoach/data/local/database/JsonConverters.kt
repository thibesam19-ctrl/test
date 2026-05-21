package com.axiom.aicoach.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

/**
 * Room TypeConverters for List<String>. Using JSON instead of CSV so that
 * values containing commas or other special characters are serialised safely.
 */
class JsonConverters {

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        runCatching { json.decodeFromString<List<String>>(value) }.getOrElse {
            // Migrate legacy comma-separated values on first read
            if (value.isBlank() || value == "[]") emptyList()
            else value.split(",").map { it.trim() }.filter { it.isNotBlank() }
        }
}
