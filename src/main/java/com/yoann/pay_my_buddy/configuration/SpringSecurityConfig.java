package com.yoann.pay_my_buddy.configuration;

import com.yoann.pay_my_buddy.controllers.TransactionController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class that defines authentication, authorization,
 * and password encoding for the application.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    private final Logger log = LogManager.getLogger(TransactionController.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Defines the HTTP security rules, including access permissions, login/logout configurations,
     * CSRF protection, and form login.
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return a configured {@link SecurityFilterChain}
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring security filter chain");
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login", "/css/*", "/logout").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/registration").anonymous();
                    auth.requestMatchers(HttpMethod.POST, "/registration").anonymous();
                    auth.anyRequest().authenticated();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/transaction", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .csrf(Customizer.withDefaults())
        ;

        return http.build();
    }

    /**
     * Defines the password encoder to use for encoding user passwords.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("Configuring BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the {@link AuthenticationManager} with the custom user details service and password encoder.
     *
     * @param http    the {@link HttpSecurity} instance
     * @param encoder the {@link BCryptPasswordEncoder} to use for password encoding
     * @return an {@link AuthenticationManager} instance
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder encoder) throws Exception {
        log.debug("Configuring AuthenticationManager");
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(encoder);

        return authenticationManagerBuilder.build();
    }
}
