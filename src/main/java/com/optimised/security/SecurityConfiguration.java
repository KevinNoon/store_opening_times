package com.optimised.security;

import com.optimised.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .httpBasic()
            .and()
            .authorizeHttpRequests(
                authorize -> {
                    authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();
                    authorize.requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg" )).permitAll();
                }
            );
        // Icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    protected void configure(WebSecurity webSecurity) throws Exception{
        webSecurity.ignoring().requestMatchers(
            AntPathRequestMatcher.antMatcher("/h2/**")
            ,AntPathRequestMatcher.antMatcher("/api/v1/**")
        );
        super.configure(webSecurity);
    }
}
