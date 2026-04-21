package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final String fromEmail;

    public EmailService(ReflectProperties properties) {
        this.resend = new Resend(properties.resend().apiKey());
        this.fromEmail = properties.resend().fromEmail();
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
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(request);
            log.info("Email sent to {} (subject: {})", to, subject);
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
