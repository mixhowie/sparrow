package com.sparrow.ssh

import com.sparrow.common.*
import org.slf4j.*
import java.util.concurrent.*

object SshCommand {
    private val logger = LoggerFactory.getLogger(SshCommand::class.java)

    fun list() {
        SwConf.ROOT.get("ssh")?.get("accounts")?.mapNotNull { it["name"] }?.forEach {
            println(it.asText())
        }
    }

    fun connect(name: String) {
        logger.info("connect ssh by name: {}", name)

        val accounts = SwConf.ROOT.get("ssh")?.get("accounts")?.map { accountJson ->
            SshAccount(
                name = accountJson["name"].asText(),
                host = accountJson["host"].asText(),
                port = accountJson["port"]?.asInt() ?: 22,
                user = accountJson["user"].asText(),
                passwd = accountJson["passwd"].asText(),
                keepalive = accountJson["keepalive"].asBoolean(),
                remark = accountJson["remark"].asText(),
                tunnels = accountJson["tunnels"]?.map { tunnelJson ->
                    SshAccount.Tunnel(
                        type = tunnelJson["type"].asText(),
                        local = tunnelJson["local"].asText(),
                        remote = tunnelJson["remote"].asText(),
                        remark = tunnelJson["remark"].asText()
                    )
                } ?: listOf()
            )
        } ?: listOf()

        val account = accounts.find { it.name == name } ?: error("not found account by name %s".format(name))

        SshConnection.connect(account)

        while (true) {
            TimeUnit.SECONDS.sleep(1)
        }
    }
}
