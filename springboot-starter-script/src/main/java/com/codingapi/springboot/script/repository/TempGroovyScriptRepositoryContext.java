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

    private TempGroovyScriptRepositoryContext(){}

    @Setter
    private TempGroovyScriptRepository tempGroovyScriptRepository = new DefaultTempGroovyScriptRepository();

    public void save(TempGroovyScript tempGroovyScript) {
        tempGroovyScriptRepository.save(tempGroovyScript);
    }

    public void delete(TempGroovyScript tempGroovyScript) {
        tempGroovyScriptRepository.delete(tempGroovyScript);
    }

    public TempGroovyScript get(String key) {
        return tempGroovyScriptRepository.get(key);
    }

    public Page<TempGroovyScript> find(PageRequest request) {
        return tempGroovyScriptRepository.find(request);
    }


}
