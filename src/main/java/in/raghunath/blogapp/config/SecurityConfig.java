package in.raghunath.blogapp.config;

import in.raghunath.blogapp.filter.JwtAuthenticationFilter;
import in.raghunath.blogapp.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/actuator/**","/swagger-ui/**","/v3/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blogs", "/api/blogs/**","/api/users", "/api/users/**" ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/blogs")
                        .hasAnyAuthority(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/blogs/**","/api/users/**").authenticated() // Allow if logged in
                        .requestMatchers(HttpMethod.DELETE, "/api/blogs/**").authenticated() // Allow if logged in
                        .requestMatchers("/api/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}