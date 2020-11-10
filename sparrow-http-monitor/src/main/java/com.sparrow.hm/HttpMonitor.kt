package com.sparrow.hm

import com.sparrow.common.*
import org.simplejavamail.api.email.*
import org.simplejavamail.email.*
import org.simplejavamail.mailer.*
import org.slf4j.*
import java.net.*
import java.util.*
import javax.mail.*

object HttpMonitor {
    private val logger = LoggerFactory.getLogger(HttpMonitor::class.java)

    private var alertCount = 0
    private var lastAlertTime: Int? = null

    fun run(args: Array<String>) {
        logger.info("start http monitor, args: {}", args.joinToString())
        val name = args[0]
        val target = args[1]
        val correctCode = args[2].toInt()

        while (true) {
            this.checkAndAlert(name, target, correctCode)
            Thread.sleep(1000 * 60)
        }
    }

    /**
     * 检查服务地址并且发送告警邮件
     *
     * @param name 服务名称
     * @param target 检查地址
     * @param correctCode 正确的 ResponseCode
     */
    private fun checkAndAlert(name: String, target: String, correctCode: Int) {
        val conn = URL(target).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 5000

        if (conn.responseCode == correctCode) {
            logger.info("service recover, reset alertCount and lastAlertTime")
            this.alertCount = 0
            this.lastAlertTime = null
            return
        }

        // 告警 3 次之后，告警时间间隔1小时
        if (alertCount >= 3 && lastAlertTime != null && Date().timestamp() - lastAlertTime!! < 60 * 60) {
            logger.info("skip this alert, alertCount: {}, lastAlertTime: {}, duration: {}",
                alertCount, lastAlertTime, Date().timestamp() - lastAlertTime!!)
            return
        }

        this.alertCount += 1
        this.lastAlertTime = Date().timestamp()
        this.sendAlertMail(
            subject = "[${name}] 服务告警，code: ${conn.responseCode}",
            text = "告警次数：%s".format(alertCount)
        )
    }


    /**
     * 发送告警邮件
     *
     * @param subject 标题
     * @param text 内容
     */
    private fun sendAlertMail(subject: String, text: String) {
        logger.info("send alert mail, subject: {}, text: {}", subject, text)
        val host = SwConf.ROOT["http-monitor"]["mail"]["host"].asText()
        val from = SwConf.ROOT["http-monitor"]["mail"]["user"].asText()
        val password = SwConf.ROOT["http-monitor"]["mail"]["password"].asText()
        val to = SwConf.ROOT["http-monitor"]["alert-user"].asText()

        val properties = System.getProperties()
        properties.setProperty("mail.smtp.host", host)
        properties.setProperty("mail.smtp.auth", "true")

        val email = EmailBuilder.startingBlank()
            .from(from)
            .to(Recipient(to, to, Message.RecipientType.TO))
            .withSubject(subject)
            .withPlainText(text)
            .buildEmail()

        val mailer = MailerBuilder
            .withSMTPServer(host, 587, from, password)
            .buildMailer()

        mailer.sendMail(email)
    }
}
