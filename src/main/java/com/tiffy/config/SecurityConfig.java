package com.tiffy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 인증/인가 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/recipes/**", "/api/user/signup", "/api/user/login").permitAll()  // 회원가입, 로그인은 인증 없이 접근 가능
                        .requestMatchers("/api/exchange/**").authenticated())  // 인증 필요
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 설정
                .formLogin((formLogin) -> formLogin.disable())
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/user/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true));  // 로그아웃 설정

        return http.build();
    }

    // CORS 설정을 위한 Bean 정의
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080"));  // Vue.js 개발 서버 도메인
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));  // 모든 헤더 허용
        configuration.setAllowCredentials(true);  // 자격 증명을 허용하여 쿠키가 전송될 수 있도록 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManger(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
