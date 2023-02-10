package com.codingapi.springboot.framework.rest.properties;

import lombok.Getter;
import lombok.Setter;

import java.net.Proxy;

@Setter
@Getter
public class HttpProxyProperties {

    private boolean enableProxy;
    private String proxyHost;
    private int proxyPort;
    private Proxy.Type proxyType;

}
