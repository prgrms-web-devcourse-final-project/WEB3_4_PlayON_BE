package com.ll.playon;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableBatchProcessing
@EnableFeignClients
public class PlayOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayOnApplication.class, args);
	}
}
