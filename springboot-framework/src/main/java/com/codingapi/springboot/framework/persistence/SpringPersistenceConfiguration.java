package com.codingapi.springboot.framework.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringPersistenceConfiguration {


    @Bean
    public PersistenceHandler persistenceHandler(@Autowired(required = false) List<IPersistenceRepository> persistenceList){
        return new PersistenceHandler(persistenceList);
    }


}
