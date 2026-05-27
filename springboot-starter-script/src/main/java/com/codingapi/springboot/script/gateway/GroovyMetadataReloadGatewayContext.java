package com.codingapi.springboot.script.gateway;

import com.codingapi.springboot.script.meta.GroovyMetadata;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GroovyMetadataReloadGatewayContext {

    private final List<GroovyMetadataReloadGateway> gateways;

    @Getter
    private final static GroovyMetadataReloadGatewayContext instance = new GroovyMetadataReloadGatewayContext();

    private GroovyMetadataReloadGatewayContext(){
        this.gateways = new ArrayList<>();
    }

    public void addGateway(GroovyMetadataReloadGateway gateway){
        this.gateways.add(gateway);
    }

    public void reload(GroovyMetadata metadata){
        for (GroovyMetadataReloadGateway gateway:gateways){
            gateway.reload(metadata);
        }
    }

}
