package com.almeidinha.app.config;

import com.almeidinha.app.services.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserService customUserService;
    private final JWTTokenHelper jwtTokenHelper;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfiguration(
            CustomUserService customUserService,
            JWTTokenHelper jwtTokenHelper,
            RestAuthenticationEntryPoint authenticationEntryPoint)
    {

        this.customUserService = customUserService;
        this.jwtTokenHelper = jwtTokenHelper;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.authorizeRequests().anyRequest().permitAll();
        /*
        httpSecurity
                .authorizeRequests((request) -> request
                        .antMatchers("/h2-console/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic();
         */

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .authorizeRequests((request) -> request
                        .antMatchers("/h2-console/**", "/api/v1/auth/login").permitAll()
                        .antMatchers(HttpMethod.OPTIONS, "/***").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JWTAuthenticationFilter(this.customUserService, this.jwtTokenHelper), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors();
        // httpSecurity.formLogin();

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
