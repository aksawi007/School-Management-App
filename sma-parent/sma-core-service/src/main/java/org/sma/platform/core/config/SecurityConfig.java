    package org.sma.platform.core.config;

import org.sma.security.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Common Security configuration for all SMA applications
 * This configuration is reusable across all microservices
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeRequests()
                // Allow Swagger UI and API docs
                .antMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v2/api-docs",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Allow actuator health check
                .antMatchers("/actuator/**").permitAll()
                // Allow auth endpoints
                .antMatchers("/auth/**", "/public/**").permitAll()
                // Allow all API endpoints (temporary for development)
                .antMatchers("/school/**", "/academic-year/**", "/student/**", "/staff/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
