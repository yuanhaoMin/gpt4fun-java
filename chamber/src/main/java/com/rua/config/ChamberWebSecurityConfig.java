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

import static com.rua.constant.ChamberControllerConstants.CHAMBER_USER_CONTROLLER_PATH;

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
                        .requestMatchers(CHAMBER_USER_CONTROLLER_PATH + "/**") //
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
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedOrigins(List.of("*"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}