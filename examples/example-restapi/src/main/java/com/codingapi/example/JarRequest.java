package com.codingapi.example;

import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Setter
@Getter
public class JarRequest {

    private String filename;
    private String content;
    private String className;

    public byte[] getUploadStream(){
        return Base64.getDecoder().decode(content.split(",")[1]);
    }
}
