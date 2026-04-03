package com.sidework.security.config;

import com.sidework.security.component.CustomAccessDeniedHandler;
import com.sidework.security.component.CustomAuthenticationEntryPoint;
import com.sidework.security.filter.JwtFilter;
import com.sidework.security.filter.LoginFilter;
import com.sidework.security.handler.CustomLogoutHandler;
import com.sidework.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sidework.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.sidework.security.oauth.service.CustomOAuth2UserService;
import com.sidework.security.service.TokenBlackListService;
import com.sidework.security.util.CookieUtil;
import com.sidework.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final CustomLogoutHandler customLogoutHandler;
    private final TokenBlackListService tokenBlackListService;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


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
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/login","/swagger-ui/**",    // Swagger UI 관련 경로
                                "/v3/api-docs/**", "/api/v1/users/email" ,"/api/v1/users", "/api/v1/reissue",
                                "/firebase-messaging-sw.js", "/fcm-test.html", "/health", "/oauth2/authorization/github", "/api/v1/payments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/regions/**", "/login/oauth2/code/github").permitAll()
                        .requestMatchers("/internal/**")
                        .access(new WebExpressionAuthorizationManager(
                                "hasIpAddress('10.0.2.41/32')"
                        ))
                        .anyRequest().authenticated()
                ).headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .oauth2Login(configure ->
                        configure.authorizationEndpoint(config -> config.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                                .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(new JwtFilter(jwtUtil, userDetailsService, tokenBlackListService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(jwtUtil, cookieUtil, authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class)
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
        configuration.setExposedHeaders(List.of("access"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}