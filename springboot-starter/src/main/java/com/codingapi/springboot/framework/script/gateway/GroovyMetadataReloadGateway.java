package com.codingapi.springboot.framework.script.gateway;

import com.codingapi.springboot.framework.script.meta.GroovyMetadata;

/**
 *  脚本元数据刷新加载对象
 */
public interface GroovyMetadataReloadGateway {

    void reload(GroovyMetadata metadata);

}
