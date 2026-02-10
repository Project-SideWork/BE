package com.sidework.security.config;

import com.sidework.security.component.CustomAccessDeniedHandler;
import com.sidework.security.component.CustomAuthenticationEntryPoint;
import com.sidework.security.filter.JwtFilter;
import com.sidework.security.filter.LoginFilter;
import com.sidework.security.handler.CustomLogoutHandler;
import com.sidework.security.service.TokenBlackListService;
import com.sidework.security.util.CookieUtil;
import com.sidework.security.util.JwtUtil;
import com.sidework.user.application.port.out.UserOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final CookieUtil cookieUtil;
    private final UserOutPort userRepository;
    private final CustomLogoutHandler customLogoutHandler;
    private final TokenBlackListService tokenBlackListService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/login", "/swagger-ui/**",    // Swagger UI 관련 경로
                                "/v3/api-docs/**", "/api/v1/users/email" ,"/api/v1/users", "/api/v1/reissue").permitAll()
                        .anyRequest().authenticated()
                ).headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .addFilterBefore(new JwtFilter(jwtUtil, userDetailsService, tokenBlackListService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(jwtUtil, cookieUtil, userRepository, authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(204))
                );

        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:63342", "https://mail.naver.com", "http://180.210.81.232:8080", "https://docktalk.co.kr", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}