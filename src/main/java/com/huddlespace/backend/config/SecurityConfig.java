package com.huddlespace.backend.config;

import com.huddlespace.backend.security.JwtAuthFilter;
import com.huddlespace.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/student/login", "/student/register").permitAll()
                .requestMatchers("/faculty/login", "/faculty/register").permitAll()
                .requestMatchers("/student/check-exists/**").permitAll()
                .requestMatchers("/faculty/check-exists/**").permitAll()
                
                .requestMatchers("/student/profile").hasRole("STUDENT")
                .requestMatchers("/student/update-password").hasRole("STUDENT")
                .requestMatchers("/student/delete").hasRole("FACULTY")
                .requestMatchers("/student/all").hasAnyRole("STUDENT", "FACULTY")
                
                .requestMatchers("/faculty/profile").hasRole("FACULTY")
                .requestMatchers("/faculty/update-password").hasRole("FACULTY")
                .requestMatchers("/faculty/delete").hasRole("FACULTY")
                .requestMatchers("/faculty/all").hasRole("FACULTY")
                .requestMatchers("/faculty/students").hasRole("FACULTY")
                
                .requestMatchers("/api/chat/**").hasAnyRole("STUDENT", "FACULTY")
                .requestMatchers("/api/forum/**").hasAnyRole("STUDENT", "FACULTY")
                .requestMatchers("/api/users/**").hasAnyRole("STUDENT", "FACULTY")
                .requestMatchers("/api/connections/**").hasAnyRole("STUDENT", "FACULTY")
                .requestMatchers("/api/profiles/**").hasAnyRole("STUDENT", "FACULTY")
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}