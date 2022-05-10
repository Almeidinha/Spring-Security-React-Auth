package com.almeidinha.app.config;

import com.almeidinha.app.services.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserService customUserService;

    public SecurityConfiguration(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.authorizeRequests().anyRequest().permitAll();
        httpSecurity
                .authorizeRequests((request) -> request
                        .antMatchers("/h2-console/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic();

        httpSecurity.formLogin();

        // h2-console
        httpSecurity
                .csrf()
                .disable()
                .headers()
                .frameOptions()
                .disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // in-memory auth
        builder
                .inMemoryAuthentication()
                .withUser("almeida")
                .password(this.passwordEncoder().encode("password"))
                .authorities("USER", "ADMIN");

        // Database Auth
        builder
                .userDetailsService(this.customUserService)
                .passwordEncoder(this.passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
