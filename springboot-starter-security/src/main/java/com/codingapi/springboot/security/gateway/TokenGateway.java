package com.codingapi.springboot.security.gateway;

import java.util.List;

public interface TokenGateway {

    Token create(String username, String iv, List<String> authorities, String extra);

    default Token create(String username, String iv, List<String> authorities) {
        return create(username, iv, authorities, null);
    }

    default Token create(String username, List<String> authorities) {
        return create(username, null, authorities, null);
    }

    default Token create(String username, List<String> authorities, String extra) {
        return create(username, null, authorities, extra);
    }

    Token parser(String sign);

}
