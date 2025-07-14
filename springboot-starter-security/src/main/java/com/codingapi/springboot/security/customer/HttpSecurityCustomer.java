package com.codingapi.springboot.security.customer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface HttpSecurityCustomer {

    void customize(HttpSecurity security) throws Exception;

}
