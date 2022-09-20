package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.framework.crypto.AESUtils;
import com.codingapi.springboot.framework.serializable.JsonSerializable;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.beans.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class Token implements JsonSerializable {

    private String username;
    private String certificate;
    private String token;
    private List<String> authorities;
    private long expireTime;
    private long remindTime;


    public Token() {
    }

    public Token(String username, String certificate, List<String> authorities, int expireValue, int remindValue) throws IOException {
        this.username = username;
        this.certificate = AESUtils.getInstance().encode(certificate);
        this.authorities = authorities;
        this.expireTime = System.currentTimeMillis() + expireValue;
        this.remindTime = System.currentTimeMillis() + remindValue;
    }


    public void verify() throws TokenExpiredException {
        if (isExpire()) {
            throw new TokenExpiredException("token expired.");
        }
    }

    public boolean isExpire() {
        return expireTime <= System.currentTimeMillis();
    }


    public String getCertificate() {
        return certificate;
    }

    @Transient
    public String getDecodePassword() throws IOException {
        return AESUtils.getInstance().decode(certificate);
    }

    public boolean canRestToken() {
        return !isExpire() && remindTime <= System.currentTimeMillis();
    }


    @Transient
    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        Collection<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return new UsernamePasswordAuthenticationToken(this, certificate, simpleGrantedAuthorities);
    }


}
