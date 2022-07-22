package com.codingapi.springboot.security;

import com.codingapi.springboot.security.configurer.HttpSecurityConfigurer;
import com.codingapi.springboot.security.filter.MyAccessDeniedHandler;
import com.codingapi.springboot.security.filter.MyLogoutHandler;
import com.codingapi.springboot.security.filter.MyLogoutSuccessHandler;
import com.codingapi.springboot.security.filter.MyUnAuthenticationEntryPoint;
import com.codingapi.springboot.security.handler.ServletExceptionHandler;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.properties.SecurityJwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
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

        return new InMemoryUserDetailsManager(admin,user);
    }


    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public HandlerExceptionResolver servletExceptionHandler(){
        return new ServletExceptionHandler();
    }



    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain filterChain(HttpSecurity http,Jwt jwt,SecurityJwtProperties properties) throws Exception {
        //before add addCorsMappings to enable cors.
        http.cors();

        http.csrf().disable();
        http.apply(new HttpSecurityConfigurer(jwt,properties));
        http
            .exceptionHandling()
                .authenticationEntryPoint(new MyUnAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .and()
            .authorizeRequests()
                .antMatchers(properties.getAuthenticatedUrl()).authenticated()
                .and()
             //default login url :/login
            .formLogin()
                .loginProcessingUrl(properties.getLoginProcessingUrl())
                .permitAll()
                .and()
             //default logout url :/logout
            .logout()
                .logoutUrl(properties.getLogoutUrl())
                .addLogoutHandler(new MyLogoutHandler())
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .permitAll();

        return http.build();
    }



    @Bean
    @ConditionalOnMissingBean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }



    @Bean
    @ConditionalOnMissingBean
    public Jwt jwt(SecurityJwtProperties properties){
        return new Jwt(properties.getJwtSecretKey(),properties.getJwtTime(),properties.getJwtRestTime());
    }



    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .exposedHeaders("Authorization","x-xsrf-token","Access-Control-Allow-Headers","Origin","Accept,X-Requested-With",
                                "Content-Type","Access-Control-Request-Method","Access-Control-Request-Headers")
                        .maxAge(1800L)
                        .allowedOrigins("*");
            }
        };
    }


    @Bean
    @ConfigurationProperties(prefix = "security.jwt.properties")
    public SecurityJwtProperties securityJwtProperties(){
        return new SecurityJwtProperties();
    }


}
