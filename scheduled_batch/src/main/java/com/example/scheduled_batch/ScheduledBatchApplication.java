package com.example.scheduled_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduledBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduledBatchApplication.class, args);
	}

}
