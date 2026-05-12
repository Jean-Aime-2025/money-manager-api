package in.business.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final RestTemplate brevoRestTemplate;

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
}