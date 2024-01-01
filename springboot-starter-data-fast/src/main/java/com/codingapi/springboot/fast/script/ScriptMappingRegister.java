package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.mapping.MvcMappingRegister;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScriptMappingRegister {

    private final MvcMappingRegister mappingRegister;

    /**
     * test dynamic mapping
     *
     * @param scriptMapping dynamic mapping
     **/
    public void addMapping(ScriptMapping scriptMapping) {
        mappingRegister.addMapping(scriptMapping.getMapping(), scriptMapping.getScriptMethod().toRequestMethod(),
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
        mappingRegister.removeMapping(url, scriptMethod.toRequestMethod());
    }



}
