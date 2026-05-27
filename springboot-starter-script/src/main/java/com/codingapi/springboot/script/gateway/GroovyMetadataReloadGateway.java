package com.codingapi.springboot.script.gateway;

import com.codingapi.springboot.script.meta.GroovyMetadata;

/**
 *  脚本元数据刷新加载对象
 */
public interface GroovyMetadataReloadGateway {

    void reload(GroovyMetadata metadata);

}
