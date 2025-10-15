package com.dsp.templateEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class TemplateEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateEngineApplication.class, args);
	}

}
