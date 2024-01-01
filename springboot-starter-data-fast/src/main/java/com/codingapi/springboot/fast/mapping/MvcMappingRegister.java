package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.jpa.JPAQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.reflect.Method;

@AllArgsConstructor
public class MvcMappingRegister {

    private final RequestMappingHandlerMapping handlerMapping;

    public MvcMappingRegister(RequestMappingHandlerMapping handlerMapping, JPAQuery JPAQuery, JdbcTemplate jdbcTemplate) {
        this.handlerMapping = handlerMapping;
        MvcRunningContext.getInstance().init(JPAQuery, new JdbcQuery(jdbcTemplate));
    }

    /**
     * add mvc mapping
     *
     * @param url           mapping url
     * @param requestMethod request method
     * @param handler       executor handler
     * @param method        executor method
     */
    public void addMapping(String url, RequestMethod requestMethod, Object handler, Method method) {

        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths(url)
                .methods(requestMethod)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .options(options)
                .build();

        handlerMapping.registerMapping(mappingInfo, handler, method);
    }

    /**
     * remove mvc mapping
     * @param url           mapping url
     * @param requestMethod request method
     */
    public void removeMapping(String url, RequestMethod requestMethod) {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths(url)
                .methods(requestMethod)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .options(options)
                .build();

        handlerMapping.unregisterMapping(mappingInfo);
    }



    /**
     * test dynamic mapping
     * @param scriptMapping dynamic mapping
     **/
    public void addMapping(ScriptMapping scriptMapping) {
        this.addMapping(scriptMapping.getMapping(), scriptMapping.getRequestMethod(),
                scriptMapping, scriptMapping.getExecuteMethod());
    }


    /**
     * test dynamic mapping
     * @param scriptMapping dynamic mapping
     * @return result
     */
    public Response test(ScriptMapping scriptMapping) {
        return scriptMapping.execute();
    }



}
