package com.courselink.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return
                httpSecurity
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/task-categories/**").hasAnyAuthority("TEACHER", "ADMIN_TEACHER")
                .requestMatchers("/api/defence-sessions/**").hasAnyAuthority("TEACHER", "ADMIN_TEACHER")
                .requestMatchers("/api/booking-slots/generate-booking-slots").hasAnyAuthority("TEACHER", "ADMIN_TEACHER")
                .requestMatchers("/api/booking-slots").hasAnyAuthority("TEACHER", "ADMIN_TEACHER", "STUDENT", "ADMIN_STUDENT")
                .requestMatchers("/api/booking-slots/generate-booking-slots").hasAnyAuthority("TEACHER", "ADMIN_TEACHER")
                .requestMatchers("/api/booking-slots/choose-booking-slot").hasAnyAuthority("STUDENT", "ADMIN_STUDENT")
                .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT")
                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}