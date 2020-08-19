package com.sparrow.common

import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.core.type.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.*
import java.text.*

/**
 * 通用常量
 */
object CommonConstant {
    const val DAY_FORMAT_PATTERN = "yyyyMMdd"
    const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    const val DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss"

    val dayFormatter = SimpleDateFormat(DAY_FORMAT_PATTERN)
    val dateFormatter = SimpleDateFormat(DATE_FORMAT_PATTERN)
    val datetimeFormatter = SimpleDateFormat(DATETIME_FORMAT_PATTERN)

    val JACKSON = jacksonObjectMapper().also {
        it.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true)
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    val MAP_TYPE = MapTypeReference()

    class MapTypeReference : TypeReference<MutableMap<String, Any>>()
}
