package com.sidework.notification.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.credentials-base64}")
	private String credentialsBase64;

	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {

		byte[] decoded = Base64.getDecoder().decode(credentialsBase64);
		InputStream serviceAccount = new ByteArrayInputStream(decoded);

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();

		FirebaseApp app = FirebaseApp.getApps().isEmpty()
			? FirebaseApp.initializeApp(options)
			: FirebaseApp.getInstance();

		return FirebaseMessaging.getInstance(app);
	}
}
