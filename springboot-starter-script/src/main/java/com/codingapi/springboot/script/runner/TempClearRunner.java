package com.codingapi.springboot.script.runner;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.script.cache.TempGroovyScriptContext;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;

public class TempClearRunner implements InitializingBean {

    public void start() {
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
        this.start();
    }
}
