package com.sparrow.common

import com.fasterxml.jackson.databind.*
import com.sparrow.common.util.StringUtil.expandPath
import java.io.*

object SwConf {
    val ROOT: JsonNode = File(expandPath("~/.sw.json")).let {
        if (it.exists() && it.isFile && it.canRead()) {
            CommonConstant.JACKSON.readTree(it)
        } else {
            CommonConstant.JACKSON.createObjectNode()
        }
    }

    val LOCAL: JsonNode = File("sw.json").let {
        if (it.exists() && it.isFile && it.canRead()) {
            CommonConstant.JACKSON.readTree(it)
        } else {
            CommonConstant.JACKSON.createObjectNode()
        }
    }
}
