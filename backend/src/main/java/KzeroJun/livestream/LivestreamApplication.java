package KzeroJun.livestream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LivestreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivestreamApplication.class, args);
	}

}
