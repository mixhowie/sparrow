package com.sparrow.ssh

import org.apache.sshd.client.*
import org.apache.sshd.client.future.*
import org.apache.sshd.client.session.*
import org.apache.sshd.common.util.net.*
import org.slf4j.*
import kotlin.concurrent.*

object SshConnection {
    private val logger = LoggerFactory.getLogger(SshConnection::class.java)

    private var connected = false
    private lateinit var client: SshClient
    private lateinit var conn: ConnectFuture
    private lateinit var session: ClientSession

    fun connect(account: SshAccount, callback: (() -> Unit)? = null) {
        logger.info("connect, account: $account")

        if (this.connected)
            error("ssh connected")

        this.client = SshClient.setUpDefaultClient().also {
            it.start()
        }

        this.conn = this.client.connect(account.user, account.host, account.port).verify()
//        this.conn.session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.MILLISECONDS, 1000)

        this.session = this.conn.session.also {
            it.addPasswordIdentity(account.passwd)

            account.tunnels.forEach { tunnel ->
                val local = tunnel.local.split(":")
                val remote = tunnel.remote.split(":")
                when (tunnel.type) {
                    "L" -> it.createLocalPortForwardingTracker(
                        SshdSocketAddress(local[0], local[1].toInt()),
                        SshdSocketAddress(remote[0], remote[1].toInt())
                    )
                    "R" -> it.createRemotePortForwardingTracker(
                        SshdSocketAddress(remote[0], remote[1].toInt()),
                        SshdSocketAddress(local[0], local[1].toInt())
                    )
                }
            }

            it.auth().verify()
        }

        this.connected = true

        timer(period = 1000 * 60) {
            session.createExecChannel("ls").open()
        }

        Runtime.getRuntime().addShutdownHook(thread(false) {
            this.disconnect()
        })

        callback?.invoke()
    }

    fun disconnect(callback: (() -> Unit)? = null) {
        logger.info("disconnect ssh, close session, conn and client")

        this.connected = false
        this.session.close()
        this.conn.cancel()
        this.client.close()

        callback?.invoke()
    }
}
