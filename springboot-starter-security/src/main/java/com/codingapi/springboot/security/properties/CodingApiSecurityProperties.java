package com.codingapi.springboot.security.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodingApiSecurityProperties {

    /**
     * 权限拦截URL
     */
    private String authenticatedUrls = "/api/**";


    /**
     * 登录接口地址
     */
    private String loginProcessingUrl = "/user/login";

    /**
     * 退出登录接口地址
     */
    private String logoutUrl = "/user/logout";

    /**
     * 放开接口地址
     */
    private String ignoreUrls = "/open/**";

    /**
     * aes key
     */
    private String aseKey = "QUNEWCQlXiYqJCNYQ1phc0FDRFgkJV4mKiQjWENaYXM=";

    /**
     * aes iv
     */
    private String aseIv = "QUNYRkdIQEVEUyNYQ1phcw==";


    /**
     * 启用禁用CSRF
     */
    private boolean disableCsrf = true;


    /**
     * 启用禁用CORS
     */
    private boolean disableCors = true;


    public String[] getIgnoreUrls() {
        return ignoreUrls.split(",");
    }

    public String[] getAuthenticatedUrls() {
        return authenticatedUrls.split(",");
    }
}
