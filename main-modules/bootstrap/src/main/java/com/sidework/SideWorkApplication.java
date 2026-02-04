package com.sidework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.sidework"})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.sidework.**.persistence.repository"})
@EnableScheduling
@EntityScan(basePackages = {"com.sidework.project.persistence.entity", "com.sidework.user.persistence.entity",
        "com.sidework.profile.persistence.entity", "com.sidework.skill.persistence.entity",
        "com.sidework.notification.persistence.entity"})
public class SideWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideWorkApplication.class, args);
	}

}
