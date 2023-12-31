package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMethod;


public class MvcMappingRegister {

    private final MvcEndpointMapping mvcEndpointMapping;

    public MvcMappingRegister(MvcEndpointMapping mvcEndpointMapping, DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        this.mvcEndpointMapping = mvcEndpointMapping;
        MvcRunningContext.getInstance().init(dynamicQuery, new JdbcQuery(jdbcTemplate));
    }


    /**
     * test dynamic mapping
     * @param SQLMapping dynamic mapping
     * @return result
     */
    public Object test(SQLMapping sqlMapping)  {
        return sqlMapping.execute();
    }

    /**
     * add dynamic mapping
     * @param SQLMapping dynamic mapping
     */
    public void addMapping(SQLMapping sqlMapping)  {
        mvcEndpointMapping.addMapping(sqlMapping.getMapping(), sqlMapping.getRequestMethod(),
                sqlMapping, sqlMapping.getExecuteMethod());
    }

    /**
     * test dynamic mapping
     * @param scriptMapping dynamic mapping
     **/
    public void addMapping(ScriptMapping scriptMapping) {
        mvcEndpointMapping.addMapping(scriptMapping.getMapping(), scriptMapping.getRequestMethod(),
                scriptMapping, scriptMapping.getExecuteMethod());
    }


    /**
     * test dynamic mapping
     * @param scriptMapping dynamic mapping
     * @return result
     */
    public Object test(ScriptMapping scriptMapping) {
        return scriptMapping.execute();
    }


    /**
     * remove mapping
     * @param mapping mapping
     * @param requestMethod request method
     */
    public void removeMapping(String mapping,RequestMethod requestMethod)  {
        mvcEndpointMapping.removeMapping(mapping,requestMethod);
    }


}
