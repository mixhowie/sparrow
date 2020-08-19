package com.sparrow.common.utils

import com.cronutils.model.*
import com.cronutils.model.definition.*
import com.cronutils.model.time.*
import com.cronutils.parser.*
import java.time.*

object CronUtil {
    private val definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
    private val parser = CronParser(definition)

    fun matchnow(cronstring: String): Boolean {
        return ExecutionTime.forCron(parser.parse(cronstring)).isMatch(ZonedDateTime.now())
    }
}
