package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *  临时脚本仓库对象
 */
public interface TempGroovyScriptRepository {

    /**
     * 保存数据
     */
    void save(TempGroovyScript tempGroovyScript);

    /**
     * 删除数据
     */
    void delete(String key);

    /**
     * 获取临时脚本
     * @param key 脚本key
     */
    TempGroovyScript get(String key);

    /**
     * 查询数据
     * @param request 分页条件
     */
    Page<TempGroovyScript> find(PageRequest request);

}
