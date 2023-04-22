package com.codingapi.springboot.security;

import com.codingapi.springboot.security.configurer.HttpSecurityConfigurer;
import com.codingapi.springboot.security.controller.VersionController;
import com.codingapi.springboot.security.filter.*;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.properties.SecurityJwtProperties;
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
    public SecurityLoginHandler securityLoginHandler(){
        return (request, response, handler) -> {
        };
    }


    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain filterChain(HttpSecurity security, Jwt jwt,SecurityLoginHandler loginHandler,
                                           SecurityJwtProperties properties) throws Exception {
        //disable basic auth
        security.httpBasic().disable();

        //before add addCorsMappings to enable cors.
        security.cors();
        if(properties.isDisableCsrf() ){
            security.csrf().disable();
        }
        security.apply(new HttpSecurityConfigurer(jwt,loginHandler,properties));
        security
                .exceptionHandling()
                .authenticationEntryPoint(new MyUnAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .and()
                .authorizeHttpRequests(
                        registry -> {
                            registry.requestMatchers(properties.getIgnoreUrls()).permitAll()
                                    .requestMatchers(properties.getAuthenticatedUrls()).authenticated()
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
    @ConditionalOnMissingBean
    public Jwt jwt(SecurityJwtProperties properties) {
        return new Jwt(properties.getJwtSecretKey(), properties.getJwtTime(), properties.getJwtRestTime());
    }


    @Bean
    public WebMvcConfigurer corsConfigurer(SecurityJwtProperties securityJwtProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if(securityJwtProperties.isDisableCors()) {
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
    public SecurityJwtProperties securityJwtProperties() {
        return new SecurityJwtProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public VersionController versionController(Environment environment){
        return new VersionController(environment);
    }

}
