package com.sparrow.ssh

data class SshAccount(
    val name: String,
    val host: String,
    val port: Int,
    val user: String,
    val passwd: String,
    val keepalive: Boolean,
    val tunnels: List<Tunnel>
) {
    data class Tunnel(
        val type: String,
        val local: String,
        val remote: String
    )
}
