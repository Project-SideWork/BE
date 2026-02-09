package com.sidework.notification.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {

	// TODO: 배포환경 경로 시크릿에서 주입할 것
	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
		try (InputStream serviceAccount =
			new ClassPathResource("firebase.json").getInputStream()) {

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			FirebaseApp app;
			if (FirebaseApp.getApps().isEmpty()) {
				app = FirebaseApp.initializeApp(options);
			} else {
				app = FirebaseApp.getInstance();
			}

			return FirebaseMessaging.getInstance(app);
		}
	}
}
