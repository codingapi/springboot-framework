package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.mapping.FastMvcMappingRegister;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FastScriptMappingRegister {

    private final FastMvcMappingRegister fastMvcMappingRegister;

    /**
     * test dynamic mapping
     *
     * @param scriptMapping dynamic mapping
     **/
    public void addMapping(ScriptMapping scriptMapping) {
        fastMvcMappingRegister.addMapping(scriptMapping.getMapping(), scriptMapping.getScriptMethod().toRequestMethod(),
                scriptMapping, scriptMapping.getExecuteMethod());
    }


    /**
     * test dynamic mapping
     *
     * @param scriptMapping dynamic mapping
     * @return result
     */
    public Response test(ScriptMapping scriptMapping) {
        return scriptMapping.execute();
    }


    /**
     * remove mvc mapping
     *
     * @param url           mapping url
     * @param requestMethod request method
     */
    public void removeMapping(String url, ScriptMethod scriptMethod){
        fastMvcMappingRegister.removeMapping(url, scriptMethod.toRequestMethod());
    }



}
