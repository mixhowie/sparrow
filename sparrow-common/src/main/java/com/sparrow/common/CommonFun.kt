package com.sparrow.common

import com.sparrow.common.CommonConstant.JACKSON
import com.sparrow.common.CommonConstant.MAP_TYPE
import org.joda.time.*
import java.text.*
import java.util.*

fun DateTime.timestamp() = (this.millis / 1000).toInt()
fun DateTime.formatDay(): String = this.toDate().formatDay()
fun DateTime.formatDate(): String = this.toDate().formatDate()
fun DateTime.formatDatetime(): String = this.toDate().formatDatetime()
fun DateTime.formatRunTime(): String {
    val day = this.millis / (1000 * 60 * 60 * 24)
    val hour = this.hourOfDay
    val minute = this.minuteOfHour
    val second = this.secondOfMinute
    return "%d day %02d:%02d:%02d".format(day, hour, minute, second)
}

fun Date.toJoda() = DateTime(this)
fun Date.timestamp(): Int = (this.time / 1000).toInt()
fun Date.format(pattern: String): String = SimpleDateFormat(pattern).format(this)
fun Date.formatDay(): String = CommonConstant.dayFormatter.format(this)
fun Date.formatDate(): String = CommonConstant.dateFormatter.format(this)
fun Date.formatDatetime(): String = CommonConstant.datetimeFormatter.format(this)

fun String.sqlFilter(): String = this.replace("'", "").replace("\"", "").replace(" ", "").replace("\t", "")

fun Number.toFixdString(fixNumber: Int) = String.format("%.${fixNumber}f", this)
fun Number.toMoneyString(): String = String.format("%.2f", this)
fun Number.toPercentageString(): String = String.format("%.2f%%", this)

fun Int.toJoda(): DateTime = DateTime(this.toLong() * 1000)
fun Int.toDate(): Date = Date(this.toLong() * 1000)
fun Long.toJoda(): DateTime = DateTime(this)
fun Long.toDate(): Date = Date(this)

inline fun <reified V> Map<*, *>.getAs(key: String) = this[key]?.let { it as V }
inline fun <reified V> Map<*, *>.getAs(key: String, default: V) = this[key]?.let { it as V } ?: default

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> Map<*, *>.getAsMap(key: String): Map<K, V>? = this[key]?.let { it as Map<K, V> }

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> Map<*, *>.getAsMap(key: String, default: Map<K, V>): Map<K, V> = this[key]?.let { it as Map<K, V> }
    ?: default

fun Any.jsonToMap(): MutableMap<String, Any> = JACKSON.convertValue(this, MAP_TYPE)
