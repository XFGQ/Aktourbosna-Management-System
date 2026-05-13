package org.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger", "/swagger-ui/**", "/swagger-ui.html",
                                "/api-docs/**", "/v3/api-docs/**", "/v3/api-docs"
                        ).permitAll()

                        // User management — ADMIN only
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Guides — GET + PUT for both roles, POST + DELETE ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/guides/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/api/guides/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers("/api/guides/**").hasRole("ADMIN")

                        // Vehicles — GET for both roles, mutations ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers("/api/vehicles/**").hasRole("ADMIN")

                        // Tours — GET, POST, PUT for both roles; DELETE ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/tours/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.POST, "/api/tours/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/api/tours/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/api/tours/**").hasRole("ADMIN")

                        // Customers — GET, POST, PUT for both roles; DELETE ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/tours/*/customers/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.POST, "/api/tours/*/customers/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/api/tours/*/customers/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/api/tours/*/customers/**").hasRole("ADMIN")

                        // Routes — GET for both roles, mutations ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/routes/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers("/api/routes/**").hasRole("ADMIN")

                        // Waypoints — GET for both roles, mutations ADMIN only
                        .requestMatchers(HttpMethod.GET, "/api/waypoints/**").hasAnyRole("ADMIN", "GUIDE")
                        .requestMatchers("/api/waypoints/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
