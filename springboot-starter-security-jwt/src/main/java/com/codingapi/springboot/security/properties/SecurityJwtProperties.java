package com.codingapi.springboot.security.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecurityJwtProperties {


    /**
     * JWT密钥
     */
    private String jwtSecretKey;

    /**
     * JWT 有效时间(毫秒)
     */
    private int jwtTime;

    /**
     * JWT 更换令牌时间(毫秒)
     */
    private int jwtRestTime;

    /**
     * 权限拦截URL
     */
    private String authenticatedUrls  = "/api/**";


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


    public String[] getIgnoreUrls(){
        return ignoreUrls.split(",");
    }

    public String[] getAuthenticatedUrls(){
        return authenticatedUrls.split(",");
    }
}
