package in.raghunath.blogapp.config;

import in.raghunath.blogapp.filter.JwtAuthenticationFilter;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // Remains the same
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Remains the same

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API (ensure other protections if needed)
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to auth endpoints
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/refresh", "/auth/logout").permitAll()
                        // Secure all other endpoints - requires a valid Access Token
                        .anyRequest().authenticated()
                )
                // No need to explicitly set userDetailsService here if AuthenticationManager is configured correctly
                // .userDetailsService(userDetailsService)
                .sessionManagement(session -> session
                        // Ensure stateless sessions - VERY IMPORTANT
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add your JWT filter to validate Access Tokens before the standard auth filters
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Good choice
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Standard way to get the AuthenticationManager bean
        return authenticationConfiguration.getAuthenticationManager();
    }
}