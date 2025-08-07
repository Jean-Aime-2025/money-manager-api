package in.business.moneymanager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MoneymanagerApplication {

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;

	public static void main(String[] args) {
		SpringApplication.run(MoneymanagerApplication.class, args);
	}

	@PostConstruct
	public void showEnvVars() {
		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
	}

}
