package com.doctor.config;

import com.doctor.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**", "/error").permitAll()
                .antMatchers("/api/dashboard/**").authenticated()
                .antMatchers("/api/cats/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/cats").hasAnyRole("ADMIN", "KEEPER")
                .antMatchers(HttpMethod.PUT, "/api/cats/**").hasAnyRole("ADMIN", "KEEPER")
                .antMatchers(HttpMethod.DELETE, "/api/cats/**").hasRole("ADMIN")
                .antMatchers("/api/feeding-records/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/feeding-records").hasRole("KEEPER")
                .antMatchers(HttpMethod.DELETE, "/api/feeding-records/**").hasRole("ADMIN")
                .antMatchers("/api/reminders/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/reminders").hasAnyRole("ADMIN", "KEEPER")
                .antMatchers(HttpMethod.PUT, "/api/reminders/**/complete").hasRole("KEEPER")
                .antMatchers(HttpMethod.DELETE, "/api/reminders/**").hasRole("ADMIN")
                .antMatchers("/api/environment/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/environment").hasRole("KEEPER")
                .anyRequest().authenticated()
            .and()
            .httpBasic().disable()
            .formLogin().disable();

        // 将 JWT 过滤器添加到 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
