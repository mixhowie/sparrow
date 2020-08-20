package com.sparrow.main

import com.sparrow.common.*
import com.sparrow.ssh.*
import org.apache.commons.cli.*
import org.slf4j.*

class Main {
    companion object {
        private val logger = LoggerFactory.getLogger(Main::class.java)

        private val options = Options().also {
            it.addOption(Option
                .builder("h")
                .longOpt("help")
                .hasArg(false)
                .build())
            it.addOption(Option
                .builder("sl")
                .longOpt("sshlist")
                .hasArg(false)
                .desc("list all ssh accounts")
                .build())
            it.addOption(Option
                .builder("sc")
                .longOpt("sshconnect")
                .hasArg(false)
                .desc("connect the remote ssh by name")
                .build())
            it.addOption(Option
                .builder("n")
                .longOpt("name")
                .hasArg()
                .argName("NAME")
                .desc("name of account")
                .build())
        }

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info(args.contentToString())
            logger.info(SwConf.ROOT.toString())

            try {
                val cmd = DefaultParser().parse(options, args)

                when {
                    cmd.hasOption("sl") -> SshCommand.list()
                    cmd.hasOption("sc") -> SshCommand.connect(cmd.getOptionValue("n"))
                    else -> this.printHelp()
                }
            } catch (e: Exception) {
                logger.error(e.toString(), e)
                this.printHelp()
            }
        }

        fun printHelp() = HelpFormatter().printHelp("sw", this.options)
    }
}
