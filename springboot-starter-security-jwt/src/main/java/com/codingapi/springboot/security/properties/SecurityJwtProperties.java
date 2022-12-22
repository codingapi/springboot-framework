package com.codingapi.springboot.security.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecurityJwtProperties {

    /**
     * JWT密钥
     * 需大于32位的字符串
     */
    private String jwtSecretKey = "codingapi.security.jwt.secretkey";


    /**
     * aes key
     */
    private String aseKey = "QUNEWCQlXiYqJCNYQ1phc0FDRFgkJV4mKiQjWENaYXM=";

    /**
     * aes iv
     */
    private String aseIv = "QUNYRkdIQEVEUyNYQ1phcw==";


    /**
     * JWT 有效时间(毫秒)
     * 15分钟有效期 1000*60*15=900000
     */
    private int jwtTime = 900000;

    /**
     * JWT 更换令牌时间(毫秒)
     * 10分钟后更换令牌 1000*60*10=600000
     */
    private int jwtRestTime = 600000;

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
