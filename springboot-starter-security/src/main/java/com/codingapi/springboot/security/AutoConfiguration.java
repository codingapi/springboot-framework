package com.codingapi.springboot.security;

import com.codingapi.springboot.security.configurer.HttpSecurityConfigurer;
import com.codingapi.springboot.security.controller.VersionController;
import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.filter.*;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode("admin");

        UserDetails admin = User.withUsername("admin")
                .password(password)
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(password)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }


    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    @ConditionalOnMissingBean
    public SecurityLoginHandler securityLoginHandler() {
        return new SecurityLoginHandler() {
            @Override
            public void preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest handler) throws Exception {

            }

            @Override
            public LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest handler, Token token) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token.getToken());
                loginResponse.setUsername(token.getUsername());
                loginResponse.setAuthorities(token.getAuthorities());
                return loginResponse;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationTokenFilter authenticationTokenFilter() {
        return (request, response) -> {

        };
    }


    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain filterChain(HttpSecurity security, TokenGateway tokenGateway, SecurityLoginHandler loginHandler,
                                           CodingApiSecurityProperties properties, AuthenticationTokenFilter authenticationTokenFilter) throws Exception {
        //disable basic auth
        security.httpBasic(AbstractHttpConfigurer::disable);

        //before add addCorsMappings to enable cors.
        security.cors(httpSecurityCorsConfigurer -> {
            if (properties.isDisableCors()) {
                httpSecurityCorsConfigurer.disable();
            }
        });

        security.csrf(httpSecurityCsrfConfigurer -> {
            if (properties.isDisableCsrf()) {
                httpSecurityCsrfConfigurer.disable();
            }
        });


        security.with(new HttpSecurityConfigurer(tokenGateway, loginHandler, properties, authenticationTokenFilter), Customizer.withDefaults());
        security.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new MyUnAuthenticationEntryPoint())
                                .accessDeniedHandler(new MyAccessDeniedHandler()))
                .authorizeHttpRequests(
                        registry -> {
                            registry.requestMatchers(properties.getIgnoreUrls()).permitAll()
                                    .requestMatchers(properties.getAuthenticatedUrls()).authenticated()
                                    .anyRequest().permitAll();
                        }
                )
                //default login url :/login
                .formLogin(httpSecurityFormLoginConfigurer ->
                        httpSecurityFormLoginConfigurer.loginPage(properties.getLoginProcessingUrl())
                )
                //default logout url :/logout
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl(properties.getLogoutUrl())
                        .addLogoutHandler(new MyLogoutHandler())
                        .logoutSuccessHandler(new MyLogoutSuccessHandler()));

        return security.build();
    }


    @Bean
    @ConditionalOnMissingBean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


    @Bean
    public WebMvcConfigurer corsConfigurer(CodingApiSecurityProperties securityJwtProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (securityJwtProperties.isDisableCors()) {
                    registry.addMapping("/**")
                            .allowedHeaders("*")
                            .allowedMethods("*")
                            .exposedHeaders("Authorization", "x-xsrf-token", "Access-Control-Allow-Headers", "Origin",
                                    "Accept,X-Requested-With", "Content-Type", "Access-Control-Request-Method",
                                    "Access-Control-Request-Headers")
                            .maxAge(1800L)
                            .allowedOrigins("*");
                }
            }
        };
    }


    @Bean
    @ConfigurationProperties(prefix = "codingapi.security")
    public CodingApiSecurityProperties codingApiSecurityProperties() {
        return new CodingApiSecurityProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public VersionController versionController(Environment environment) {
        return new VersionController(environment);
    }

}
