package com.sparrow.main

import org.slf4j.*

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logger = LoggerFactory.getLogger("main")

            logger.info(args.contentToString())
        }
    }
}