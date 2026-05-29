package com.codingapi.springboot.script.repository.impl;

import com.codingapi.springboot.script.repository.TempGroovyScriptRepository;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public class DefaultTempGroovyScriptRepository implements TempGroovyScriptRepository {

    private final Map<String, TempGroovyScript> groovyScripts;

    public DefaultTempGroovyScriptRepository() {
        this.groovyScripts = new HashMap<>();
    }

    @Override
    public void save(TempGroovyScript tempGroovyScript) {
        groovyScripts.put(tempGroovyScript.getKey(), tempGroovyScript);
    }

    @Override
    public void delete(TempGroovyScript tempGroovyScript) {
        groovyScripts.remove(tempGroovyScript.getKey());
    }

    @Override
    public TempGroovyScript get(String key) {
        return groovyScripts.get(key);
    }

    @Override
    public Page<TempGroovyScript> find(PageRequest request) {
        int form = request.getPageNumber() * request.getPageSize();
        int to = (request.getPageNumber() + 1) * request.getPageSize();

        List<TempGroovyScript> list = this.groovyScripts.values()
                .stream()
                .sorted(Comparator.comparing(TempGroovyScript::getClearTime))
                .toList();

        List<TempGroovyScript> data;
        if (form > list.size()) {
            data = new ArrayList<>();
        } else {
            if (list.size() > to) {
                data = list.subList(form, to);
            } else {
                data = list.subList(form, list.size());
            }
        }
        return new PageImpl<>(data, request, this.groovyScripts.size());
    }
}
