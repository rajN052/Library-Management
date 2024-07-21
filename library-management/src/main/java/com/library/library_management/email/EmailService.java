package com.library.library_management.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        // Determine template name based on emailTemplate (if needed)
        String templateName = (emailTemplate == null) ? "activate_account" : "activate_account";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());

        // Prepare context variables for Thymeleaf template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);
        log.info(activationCode);
        Context context = new Context();
        context.setVariables(properties);

        // Set email metadata
        helper.setFrom("contact@aliboucoding.com");
        helper.setTo(to);
        helper.setSubject(subject);

        // Process the Thymeleaf template with the provided context variables
        String template = templateEngine.process(templateName, context);

        // Set the HTML content of the email
        helper.setText(template, true);

        // Send the email
        mailSender.send(mimeMessage);

        // Log success message
        log.info("Email successfully sent to {}: {}", to, subject);
    }
}
