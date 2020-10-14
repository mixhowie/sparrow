package com.sparrow.ssh

import com.jcraft.jsch.*
import org.slf4j.*
import kotlin.concurrent.*

object SshConnection {
    private val logger = LoggerFactory.getLogger(SshConnection::class.java)

    private var connected = false

    fun connect(account: SshAccount) {
        logger.info("connect, account: $account")

        if (this.connected)
            error("ssh connected")

        val jsch = JSch()
        val session = jsch.getSession(account.user, account.host, account.port)
        session.setPassword(account.passwd)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()
        account.tunnels.forEach { tunnel ->
            val local = tunnel.local.split(":")
            val remote = tunnel.remote.split(":")
            when (tunnel.type) {
                "L" -> {
                    logger.info("create local -> remote forwarding tracker, %s -> %s, %s".format(
                        tunnel.local, tunnel.remote, tunnel.remark
                    ))
                    session.setPortForwardingL(local[0], local[1].toInt(), remote[0], remote[1].toInt())
                }
                "R" -> {
                    logger.info("create remote -> local forwarding tracker, %s -> %s, %s".format(
                        tunnel.remote, tunnel.local, tunnel.remark
                    ))
                    session.setPortForwardingR(remote[0], remote[1].toInt(), local[0], local[1].toInt())
                }
            }
        }

        this.connected = true

        timer(period = 1000 * 60) {
            session.sendKeepAliveMsg()
        }
    }
}
