package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String NOT_CONFIGURED = "not-configured";

    private final Resend resend;
    private final String fromEmail;
    private final JavaMailSender mailSender;
    private final boolean useMailSender;

    public EmailService(ReflectProperties properties, JavaMailSender mailSender) {
        String apiKey = properties.resend().apiKey();
        this.useMailSender = NOT_CONFIGURED.equals(apiKey);
        this.resend = useMailSender ? null : new Resend(apiKey);
        this.fromEmail = properties.resend().fromEmail();
        this.mailSender = mailSender;
        if (useMailSender) {
            log.info("Resend API key not configured — using JavaMailSender (MailHog) for local email delivery");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken, String frontendBaseUrl) {
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + resetToken;
        String html = """
                <div style="font-family: system-ui, sans-serif; max-width: 480px; margin: 0 auto;">
                  <h2 style="font-size: 20px; font-weight: 600;">Reset your password</h2>
                  <p style="color: #475569; line-height: 1.6;">
                    Someone requested a password reset for your Reflect account. Click below to choose a new password.
                  </p>
                  <a href="%s"
                     style="display: inline-block; background: #4F46E5; color: white; padding: 10px 24px;
                            border-radius: 8px; text-decoration: none; font-weight: 500; margin: 16px 0;">
                    Reset password
                  </a>
                  <p style="color: #94A3B8; font-size: 13px; line-height: 1.5;">
                    This link expires in 24 hours. If you didn't request this, you can safely ignore this email.
                  </p>
                </div>
                """.formatted(resetUrl);

        sendEmail(toEmail, "Reset your Reflect password", html);
    }

    public void sendVerificationEmail(String toEmail, String verifyToken, String frontendBaseUrl) {
        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + verifyToken;
        String html = """
                <div style="font-family: system-ui, sans-serif; max-width: 480px; margin: 0 auto;">
                  <h2 style="font-size: 20px; font-weight: 600;">Verify your email</h2>
                  <p style="color: #475569; line-height: 1.6;">
                    Welcome to Reflect. Please verify your email address to complete your account setup.
                  </p>
                  <a href="%s"
                     style="display: inline-block; background: #4F46E5; color: white; padding: 10px 24px;
                            border-radius: 8px; text-decoration: none; font-weight: 500; margin: 16px 0;">
                    Verify email
                  </a>
                  <p style="color: #94A3B8; font-size: 13px; line-height: 1.5;">
                    This link expires in 48 hours.
                  </p>
                </div>
                """.formatted(verifyUrl);

        sendEmail(toEmail, "Verify your Reflect email", html);
    }

    public void sendReminderEmail(String toEmail, String displayName, String frontendBaseUrl) {
        String checkinUrl = frontendBaseUrl + "/check-in";
        String html = """
                <div style="font-family: system-ui, sans-serif; max-width: 480px; margin: 0 auto;">
                  <h2 style="font-size: 20px; font-weight: 600;">Time to reflect, %s</h2>
                  <p style="color: #475569; line-height: 1.6;">
                    Your weekly check-in is waiting. Take a few minutes to look back on your week — what moved forward, where you felt resistance, and what matters most next.
                  </p>
                  <a href="%s"
                     style="display: inline-block; background: #4F46E5; color: white; padding: 10px 24px;
                            border-radius: 8px; text-decoration: none; font-weight: 500; margin: 16px 0;">
                    Start your check-in
                  </a>
                  <p style="color: #94A3B8; font-size: 13px; line-height: 1.5;">
                    You're receiving this because you have reminders enabled in Reflect.
                    You can turn them off in your account settings.
                  </p>
                </div>
                """.formatted(displayName, checkinUrl);

        sendEmail(toEmail, "Your weekly check-in is ready", html);
    }

    private void sendEmail(String to, String subject, String html) {
        if (useMailSender) {
            sendViaMailSender(to, subject, html);
        } else {
            sendViaResend(to, subject, html);
        }
    }

    private void sendViaMailSender(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent via MailHog to {} (subject: {})", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email via MailHog to {}: {}", to, e.getMessage());
        }
    }

    private void sendViaResend(String to, String subject, String html) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(request);
            log.info("Email sent via Resend to {} (subject: {})", to, subject);
        } catch (ResendException e) {
            log.error("Failed to send email via Resend to {}: {}", to, e.getMessage());
        }
    }
}
