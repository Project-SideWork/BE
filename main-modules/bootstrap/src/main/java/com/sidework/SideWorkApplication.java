package com.sidework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sidework"})
public class SideWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideWorkApplication.class, args);
	}

}
