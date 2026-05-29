package com.codingapi.springboot.script.runner;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
public class GroovyScriptEngineRunner implements InitializingBean, DisposableBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("load temp groovy script data to cache ...");
        PageRequest request = PageRequest.of(0, 100);
        Page<TempGroovyScript> page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        while (page.hasNext()) {
            TempGroovyScriptContext.getInstance().loadAll(page.getContent());
            request = PageRequest.of(request.getCurrent() + 1, 100);
            page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        }
        log.info("load temp groovy script data to cache success.");
    }

    @Override
    public void destroy() throws Exception {
        log.info("save temp groovy script data to disk ...");

        List<TempGroovyScript> tempGroovyScriptList = TempGroovyScriptContext.getInstance().findAll();
        if(!tempGroovyScriptList.isEmpty()){
            for (TempGroovyScript groovyScript:tempGroovyScriptList){
                TempGroovyScriptRepositoryContext.getInstance().save(groovyScript);
            }
        }

        log.info("save temp groovy script data to disk success.");
    }
}
