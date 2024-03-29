package com.codingapi.springboot.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecurityJWTProperties {


    /**
     * 是否启用JWT
     */
    private boolean enable = true;

    /**
     * JWT密钥
     * 需大于32位的字符串
     */
    private String secretKey = "codingapi.security.jwt.secretkey";


    /**
     * JWT 有效时间(毫秒)
     * 15分钟有效期 1000*60*15=900000
     */
    private int validTime = 900000;

    /**
     * JWT 更换令牌时间(毫秒)
     * 10分钟后更换令牌 1000*60*10=600000
     */
    private int restTime = 600000;

}
