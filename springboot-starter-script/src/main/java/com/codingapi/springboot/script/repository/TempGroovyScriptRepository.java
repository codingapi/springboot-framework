package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TempGroovyScriptRepository {

    void save(TempGroovyScript tempGroovyScript);

    void delete(TempGroovyScript tempGroovyScript);

    TempGroovyScript get(String key);

    Page<TempGroovyScript> find(PageRequest request);
}
