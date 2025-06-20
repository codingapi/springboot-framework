package com.codingapi.springboot.security;

import com.codingapi.springboot.security.configurer.HttpSecurityConfigurer;
import com.codingapi.springboot.security.controller.VersionController;
import com.codingapi.springboot.security.customer.DefaultHttpSecurityCustomer;
import com.codingapi.springboot.security.customer.HttpSecurityCustomer;
import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.filter.*;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public AuthenticationTokenFilter authenticationTokenFilter() {
        return (request, response) -> {

        };
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpSecurityCustomer httpSecurityCustomer(CodingApiSecurityProperties properties){
        return new DefaultHttpSecurityCustomer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityLoginHandler securityLoginHandler() {
        return new SecurityLoginHandler() {
            @Override
            public void preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest handler) throws Exception {

            }

            @Override
            public LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest,UserDetails userDetails, Token token) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setUsername(token.getUsername());
                loginResponse.setToken(token.getToken());
                loginResponse.setAuthorities(token.getAuthorities());
                return loginResponse;
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain filterChain(HttpSecurity security,
                                           HttpSecurityCustomer httpSecurityCustomer,
                                           TokenGateway tokenGateway,
                                           SecurityLoginHandler loginHandler,
                                           CodingApiSecurityProperties properties,
                                           AuthenticationTokenFilter authenticationTokenFilter) throws Exception {

        httpSecurityCustomer.customize(security);

        security.apply(new HttpSecurityConfigurer(tokenGateway, loginHandler, properties, authenticationTokenFilter));
        security
                .exceptionHandling()
                .authenticationEntryPoint(new MyUnAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .and()
                .authorizeHttpRequests(
                        registry -> {
                            registry.antMatchers(properties.getIgnoreUrls()).permitAll()
                                    .antMatchers(properties.getAuthenticatedUrls()).authenticated()
                                    .anyRequest().permitAll();
                        }
                )
                //default login url :/login
                .formLogin()
                .loginProcessingUrl(properties.getLoginProcessingUrl())
                .permitAll()
                .and()
                //default logout url :/logout
                .logout()
                .logoutUrl(properties.getLogoutUrl())
                .addLogoutHandler(new MyLogoutHandler())
                .logoutSuccessHandler(new MyLogoutSuccessHandler());

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
    public WebMvcConfigurer corsConfigurer(CodingApiSecurityProperties securityProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (securityProperties.isDisableCors()) {
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
