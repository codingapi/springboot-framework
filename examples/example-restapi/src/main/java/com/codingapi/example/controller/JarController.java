package com.codingapi.example.controller;

import com.codingapi.example.JarRequest;
import com.codingapi.springboot.framework.boot.DynamicApplication;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.utils.ClassLoaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/jar/")
public class JarController {

    @PostMapping("/upload")
    public Response upload(@RequestBody JarRequest request) throws Exception {
        String jarFile = "./jars/" + request.getFilename();
        IOUtils.write(request.getUploadStream(), Files.newOutputStream(Paths.get(jarFile)));

        URLClassLoader classLoader = ClassLoaderUtils.createClassLoader(jarFile);
        Class<?> clazz = classLoader.loadClass(request.getClassName());
        log.info("class:{}",clazz);

        return Response.buildSuccess();
    }


    @PostMapping("/restart")
    public Response restart() {
        DynamicApplication.restart();
        return Response.buildSuccess();
    }
}
