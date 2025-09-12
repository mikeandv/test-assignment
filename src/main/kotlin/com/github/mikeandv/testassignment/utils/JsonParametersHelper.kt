package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.ChangeTeamRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

fun changePayloadParameterValue(testData: Any, path: List<String>, newValue: Any?): String {
    val requestAsJson = when (testData) {
        is AssignLicenseRequest -> Json.encodeToJsonElement(testData).jsonObject
        is ChangeTeamRequest -> Json.encodeToJsonElement(testData).jsonObject
        else -> throw IllegalArgumentException("Unsupported request type: ${testData::class}")
    }
    val updatedJson = requestAsJson.replace(path, anyToJsonPrimitive(newValue))
    return Json.encodeToString(updatedJson)
}

fun getEmptyJsonObject(): String {
    return JsonObject(emptyMap()).toString()
}

fun removePayloadParameterValue(testData: Any, path: List<String>): String {
    val requestAsJson = when (testData) {
        is AssignLicenseRequest -> Json.encodeToJsonElement(testData).jsonObject
        is ChangeTeamRequest -> Json.encodeToJsonElement(testData).jsonObject
        else -> throw IllegalArgumentException("Unsupported request type: ${testData::class}")
    }
    val updatedJson = removeFieldRecursive(requestAsJson, path)
    return Json.encodeToString(updatedJson)
}

private fun anyToJsonPrimitive(value: Any?): JsonElement = when (value) {
    null -> JsonNull
    is String -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is Unit -> JsonObject(emptyMap())
    else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
}

private fun JsonObject.replace(path: List<String>, newValue: JsonElement): JsonObject {
    if (path.isEmpty()) return this
    val key = path.first()
    return if (path.size == 1) {
        this.toMutableMap().apply { put(key, newValue) }.let { JsonObject(it) }
    } else {
        val child = this[key]?.jsonObject ?: JsonObject(emptyMap())
        this.toMutableMap().apply {
            put(key, child.replace(path.drop(1), newValue))
        }.let { JsonObject(it) }
    }
}

private fun removeFieldRecursive(element: JsonElement, path: List<String>): JsonElement {
    if (path.isEmpty()) return element

    val key = path.first()

    return when (element) {
        is JsonObject -> {
            if (path.size == 1) {
                JsonObject(element.toMutableMap().apply { remove(key) })
            } else {
                val child = element[key] ?: return element
                JsonObject(
                    element.toMutableMap().apply {
                        put(key, removeFieldRecursive(child, path.drop(1)))
                    }
                )
            }
        }

        is JsonArray -> JsonArray(element.map { removeFieldRecursive(it, path) })
        else -> element
    }
}