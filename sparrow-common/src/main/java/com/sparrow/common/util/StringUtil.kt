package com.sparrow.common.util

object StringUtil {
    private const val chars = "1234567890abcdefghijklmnopqrstuvwxyz"

    fun randomString(length: Int): String = (0..length).map { chars.random() }.joinToString("")

    fun expandPath(path: String): String = path.replaceFirst(Regex("^~"), System.getProperty("user.home"))
}
