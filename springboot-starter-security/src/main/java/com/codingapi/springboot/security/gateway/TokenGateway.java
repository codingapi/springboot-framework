package com.codingapi.springboot.security.gateway;

import java.util.List;

public interface TokenGateway {

    Token create(String username, String password, List<String> authorities, String extra);

    Token parser(String sign);

}
