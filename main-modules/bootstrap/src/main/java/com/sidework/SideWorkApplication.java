package com.sidework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.sidework"})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.sidework.project.persistence.repository", "com.sidework.user.persistence.repository"})
@EntityScan(basePackages = {"com.sidework.project.persistence.entity", "com.sidework.user.persistence.entity"})
public class SideWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideWorkApplication.class, args);
	}

}
