package com.codingapi.springboot.script.runner;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.script.cache.TempGroovyScriptContext;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepository;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
public class TempClearRunner implements InitializingBean, DisposableBean {

    public void addTempCache() {
        log.info("init temp cache");
        PageRequest request = PageRequest.of(0, 100);
        Page<TempGroovyScript> page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        while (page.hasNext()) {
            TempGroovyScriptContext.getInstance().loadAll(page.getContent());
            request = PageRequest.of(request.getCurrent() + 1, 100);
            page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addTempCache();
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy temp cache");

        List<TempGroovyScript> tempGroovyScriptList = TempGroovyScriptContext.getInstance().findAll();
        if(!tempGroovyScriptList.isEmpty()){
            for (TempGroovyScript groovyScript:tempGroovyScriptList){
                TempGroovyScriptRepositoryContext.getInstance().save(groovyScript);
            }
        }
    }
}
