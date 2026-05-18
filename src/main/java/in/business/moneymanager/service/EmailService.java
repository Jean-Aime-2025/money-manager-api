package in.business.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final RestTemplate brevoRestTemplate;
    private final JavaMailSenderImpl mailSender;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void sendEmail(String toEmail, String subject, String htmlContent) {

        Map<String, Object> body = new HashMap<>();

        body.put("sender", Map.of(
                "email", senderEmail,
                "name", senderName
        ));

        body.put("to", List.of(Map.of("email", toEmail)));
        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

        try {
            brevoRestTemplate.postForEntity("/smtp/email", body, String.class);
            log.info("Email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Brevo email failed: {}", e.getMessage(), e);
            throw new RuntimeException("Email failed", e);
        }
    }

    public void sendEmailWithAttachment(
            String toEmail,
            String subject,
            String htmlContent,
            byte[] attachment,
            String filename
    ) {
        try {

            String base64File = Base64.getEncoder().encodeToString(attachment);

            Map<String, Object> body = new HashMap<>();

            body.put("sender", Map.of(
                    "email", senderEmail,
                    "name", senderName
            ));

            body.put("to", List.of(Map.of("email", toEmail)));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            body.put("attachment", List.of(
                    Map.of(
                            "name", filename,
                            "content", base64File
                    )
            ));

            brevoRestTemplate.postForEntity("/smtp/email", body, String.class);

            log.info("Email with attachment sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Brevo attachment email failed: {}", e.getMessage(), e);
            throw new RuntimeException("Email with attachment failed", e);
        }
    }
}