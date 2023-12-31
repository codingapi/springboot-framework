package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.fast.script.DynamicScript;
import com.codingapi.springboot.fast.script.ScriptContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public class DynamicScriptRegister {

    private final MvcEndpointMapping mvcEndpointMapping;


    public DynamicScriptRegister(MvcEndpointMapping mvcEndpointMapping, DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        this.mvcEndpointMapping = mvcEndpointMapping;
        ScriptContext.getInstance().init(dynamicQuery, new JdbcQuery(jdbcTemplate));
    }


    /**
     * test dynamic mapping
     * @param dynamicScript dynamic mapping
     **/
    public void addMapping(DynamicScript dynamicScript) {
        mvcEndpointMapping.addMapping(dynamicScript.getMapping(), dynamicScript.getRequestMethod(),
                dynamicScript, dynamicScript.getExecuteMethod());
    }

    /**
     *  remove mapping
     * @param mapping mapping
     * @param requestMethod requestMethod
     */
    public void removeMapping(String mapping, RequestMethod requestMethod) {
        mvcEndpointMapping.removeMapping(mapping, requestMethod);
    }

    /**
     * test dynamic mapping
     * @param dynamicScript dynamic mapping
     * @return result
     */
    @ResponseBody
    public Object test(DynamicScript dynamicScript) {
        return dynamicScript.execute();
    }

}
