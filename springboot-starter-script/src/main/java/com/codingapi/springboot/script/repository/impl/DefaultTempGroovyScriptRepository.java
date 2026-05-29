package com.codingapi.springboot.script.repository.impl;

import com.codingapi.springboot.script.repository.TempGroovyScriptRepository;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultTempGroovyScriptRepository implements TempGroovyScriptRepository {

    private final Map<String, TempGroovyScript> data;

    public DefaultTempGroovyScriptRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(TempGroovyScript tempGroovyScript) {
        data.put(tempGroovyScript.getKey(), tempGroovyScript);
    }


    @Override
    public void delete(String key) {
        this.data.remove(key);
    }

    @Override
    public TempGroovyScript get(String key) {
        return data.get(key);
    }

    @Override
    public Page<TempGroovyScript> find(PageRequest request) {
        int form = request.getPageNumber() * request.getPageSize();
        int to = (request.getPageNumber() + 1) * request.getPageSize();

        List<TempGroovyScript> list = this.data.values()
                .stream()
                .sorted(Comparator.comparing(TempGroovyScript::getClearTime))
                .collect(Collectors.toList());

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
        return new PageImpl<>(data, request, this.data.size());
    }
}
