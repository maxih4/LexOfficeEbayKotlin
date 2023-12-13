package controller

import at.quickme.kotlinmailer.data.SMTPLoginInfo
import at.quickme.kotlinmailer.delivery.MailerManager
import at.quickme.kotlinmailer.delivery.mailerBuilder
import at.quickme.kotlinmailer.delivery.send
import at.quickme.kotlinmailer.email.emailBuilder
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.simplejavamail.api.mailer.config.TransportStrategy
import storage.kvstore


class MailController : KoinComponent {
    private val store: kvstore by inject()
    private val settings = store.settings

    private val host = settings.getString("smtpHost", "")
    private val port = settings.getString("port", "")
    private val username = settings.getString("usernameSMTP", "")
    private val password = settings.getString("passwordSMTP", "")


    private val mailer =
        mailerBuilder(SMTPLoginInfo(host, port.toInt(), username, password)) {
            withTransportStrategy(
                TransportStrategy.SMTP_TLS
            )
        }

    suspend fun sendMail(from: String, to: String, subject: String, content: String): Job {
        MailerManager.defaultMailer = mailer
        val email = emailBuilder {
            from(from)
            to(to)

            withSubject(subject)
            withPlainText(content)
        }
        return email.send()

    }

}