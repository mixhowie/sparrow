package com.sparrow.common.utils

object StringUtil {
    private const val chars = "1234567890abcdefghijklmnopqrstuvwxyz"
    fun randomString(length: Int): String = (0..length).map { chars.random() }.joinToString("")
}
