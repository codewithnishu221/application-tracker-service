package application.tracker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationTrackerApplication.class, args);
	}

}
