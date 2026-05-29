package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.repository.impl.DefaultTempGroovyScriptRepository;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class TempGroovyScriptRepositoryContext {

    @Getter
    private final static TempGroovyScriptRepositoryContext instance = new TempGroovyScriptRepositoryContext();

    private TempGroovyScriptRepositoryContext(){
        this.tempGroovyScriptRepository = new DefaultTempGroovyScriptRepository();
    }

    @Setter
    private TempGroovyScriptRepository tempGroovyScriptRepository;

    public void save(TempGroovyScript tempGroovyScript) {
        tempGroovyScriptRepository.save(tempGroovyScript);
    }

    public void delete(String key) {
        tempGroovyScriptRepository.delete(key);
    }

    public TempGroovyScript get(String key) {
        return tempGroovyScriptRepository.get(key);
    }

    public Page<TempGroovyScript> find(PageRequest request) {
        return tempGroovyScriptRepository.find(request);
    }


}
