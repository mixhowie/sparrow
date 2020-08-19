package com.sparrow.common.utils

import org.apache.http.*
import org.apache.http.client.config.*
import org.apache.http.client.entity.*
import org.apache.http.client.methods.*
import org.apache.http.client.protocol.*
import org.apache.http.config.*
import org.apache.http.conn.socket.*
import org.apache.http.conn.ssl.*
import org.apache.http.entity.*
import org.apache.http.impl.client.*
import org.apache.http.impl.conn.*
import org.apache.http.message.*
import org.apache.http.protocol.*
import org.apache.http.ssl.SSLContexts
import org.slf4j.*
import java.net.*
import java.util.*
import java.util.concurrent.*
import javax.net.ssl.*
import kotlin.concurrent.*

object HttpUtil {
    private val logger = LoggerFactory.getLogger(HttpUtil::class.java)
    private const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36"
    private val connectionManagerTimer = Timer()
    private val clientConfig = ClientConfig()
    private val client = HttpClientBuilder
        .create()
        .setConnectionManager(createConnectionManager())
        .setUserAgent(DEFAULT_USER_AGENT)
        .build()

    fun createConnectionManager(registry: Registry<ConnectionSocketFactory>? = null): PoolingHttpClientConnectionManager {
        val manager = if (registry == null)
            PoolingHttpClientConnectionManager()
        else
            PoolingHttpClientConnectionManager(registry)

        manager.defaultMaxPerRoute = clientConfig.defaultMaxPerRoute
        manager.maxTotal = clientConfig.maxTotal
        manager.closeExpiredConnections()

        connectionManagerTimer.schedule(timerTask {
            manager.closeExpiredConnections()
            manager.closeIdleConnections(clientConfig.connectionIdleTimeout, TimeUnit.MILLISECONDS)
        }, clientConfig.checkDelay, clientConfig.checkPeriod)

        logger.info("create connection manager, defaultMaxPerRoute: {}, maxTotal: {}, checkPeriod: {}",
            manager.defaultMaxPerRoute, manager.maxTotal, clientConfig.checkPeriod)

        return manager
    }

    fun createSocketProxyConnectionManager(): PoolingHttpClientConnectionManager {
        val registry = RegistryBuilder
            .create<ConnectionSocketFactory>()
            .register("http", LocalConnectionSocket())
            .register("https", LocalSSLConnectionSocket(SSLContexts.createSystemDefault()))
            .build()
        return createConnectionManager(registry)
    }

    fun createSocketProxyContext(
        host: String,
        port: Int
    ): HttpClientContext {
        return HttpClientContext.create().also {
            it.setAttribute("socks.address", InetSocketAddress(host, port))
        }
    }

    fun get(
        url: String,
        params: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf()
    ): HttpResponse {
        val request = HttpGet().also {
            it.uri = buildUri(url, params)
            headers.forEach { (name, value) ->
                it.addHeader(name, value)
            }
        }
        return client.execute(request)
    }

    fun postJson(
        url: String,
        data: String,
        headers: Map<String, String>
    ): HttpResponse {
        val request = HttpPost().also {
            it.uri = URI.create(url)
            it.entity = StringEntity(data, ContentType.APPLICATION_JSON)
            headers.forEach { (name, value) ->
                it.addHeader(name, value)
            }
        }
        return client.execute(request)
    }

    fun postForm(
        url: String,
        data: Map<String, String>,
        headers: Map<String, String>
    ): HttpResponse {
        val request = HttpPost().also {
            it.uri = URI.create(url)
            it.entity = UrlEncodedFormEntity(data.map { item -> BasicNameValuePair(item.key, item.value) })
            headers.forEach { (name, value) ->
                it.addHeader(name, value)
            }
        }
        return client.execute(request)
    }

    private fun buildUri(url: String, params: Map<String, String>): URI {
        val urlWithParams = if (params.isEmpty()) url else url + "?" + params.entries.joinToString("&") { it.key + "=" + it.value }
        return URI.create(urlWithParams)
    }

    private class LocalConnectionSocket : PlainConnectionSocketFactory() {
        override fun createSocket(context: HttpContext): Socket {
            val socksaddr = context.getAttribute("socks.address") as InetSocketAddress
            val proxy = Proxy(Proxy.Type.SOCKS, socksaddr)
            return Socket(proxy)
        }

        override fun connectSocket(
            connectTimeout: Int,
            socket: Socket,
            host: HttpHost,
            remoteAddress: InetSocketAddress,
            localAddress: InetSocketAddress?,
            context: HttpContext
        ): Socket {
            val unresolvedRemote = InetSocketAddress.createUnresolved(host.hostName, remoteAddress.port)
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context)
        }
    }

    private class LocalSSLConnectionSocket(sslContext: SSLContext) : SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE) {
        override fun createSocket(context: HttpContext): Socket {
            val socksaddr = context.getAttribute("socks.address") as InetSocketAddress
            val proxy = Proxy(Proxy.Type.SOCKS, socksaddr)
            return Socket(proxy)
        }

        override fun connectSocket(
            connectTimeout: Int,
            socket: Socket,
            host: HttpHost,
            remoteAddress: InetSocketAddress,
            localAddress: InetSocketAddress?,
            context: HttpContext
        ): Socket {
            val unresolvedRemote = InetSocketAddress.createUnresolved(host.hostName, remoteAddress.port)
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context)
        }
    }

    class ClientConfig : RequestConfig() {
        val defaultMaxPerRoute = 500
        val maxTotal = 1000
        val connectionIdleTimeout = 5000L
        val checkDelay = 3000L
        val checkPeriod = 3000L
    }
}
