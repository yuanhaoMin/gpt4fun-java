package com.rua.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.rua.constant.ChamberPathConstants.*;

@Configuration
@EnableWebSecurity
public class ChamberWebSecurityConfig {

    private final AuthenticationEntryPoint authEntryPoint;

    // Lombok @RequiredArgsConstructor do not bring in @Qualifier, create constructor manually
    @Autowired
    public ChamberWebSecurityConfig(
            @Qualifier("chamberDelegatedAuthenticationEntryPoint") final AuthenticationEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http //
                .httpBasic() //
                .and() //
                .authorizeHttpRequests(request -> request //
                        .requestMatchers( //
                                // Permit all user related requests
                                CHAMBER_USER_CONTROLLER_PATH + "/**",
                                // Permit chat completion with stream requests since SSE does not support auth
                                CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH + CHAMBER_CHAT_COMPLETION_CHAT_COMPLETION_WITH_STREAM_PATH,
                                // Permit completion with stream requests since SSE does not support auth
                                CHAMBER_COMPLETION_CONTROLLER_PATH + CHAMBER_COMPLETION_COMPLETION_WITH_STREAM_PATH) //
                        .permitAll() // Permit all user related requests
                        .anyRequest().authenticated()) // Other requests must be authenticated
                .cors() // Cors must be enabled with custom CorsConfigurationSource to allow all origins
                .and() //
                .csrf().disable() //
                .exceptionHandling() //
                .authenticationEntryPoint(authEntryPoint); //
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var source = new UrlBasedCorsConfigurationSource();
        final var configuration = new CorsConfiguration();
        // This is needed when client is using basic auth
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedOrigins(List.of("*"));
        // Allow any mapping
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}