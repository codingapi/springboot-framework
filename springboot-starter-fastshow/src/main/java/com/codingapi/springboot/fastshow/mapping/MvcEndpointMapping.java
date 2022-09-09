package com.codingapi.springboot.fastshow.mapping;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.reflect.Method;

@AllArgsConstructor
public class MvcEndpointMapping {

    private final RequestMappingHandlerMapping handlerMapping;

    public void addGetMapping(String url, Object handler, Method method) {

        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths(url)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .options(options)
                .build();

        handlerMapping.registerMapping(mappingInfo, handler, method);
    }

}
