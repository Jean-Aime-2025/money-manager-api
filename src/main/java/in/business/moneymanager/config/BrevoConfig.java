package in.business.moneymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BrevoConfig {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate brevoRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}