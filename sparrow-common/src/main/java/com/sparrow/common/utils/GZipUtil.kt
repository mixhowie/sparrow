package com.sparrow.common.utils

import java.io.*
import java.util.zip.*


object GZipUtil {
    /**
     * 压缩字符串
     */
    fun compress(str: String): ByteArray {
        val out = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(out)
        gzip.write(str.toByteArray())
        gzip.close()
        return out.toByteArray()
    }

    /**
     * 解压缩字节数组
     */
    fun uncompress(b: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        val `in` = ByteArrayInputStream(b)
        val gunzip = GZIPInputStream(`in`)
        val buffer = ByteArray(256)
        var n: Int
        while (gunzip.read(buffer).also { n = it } >= 0) {
            out.write(buffer, 0, n)
        }
        return out.toByteArray()
    }
}
