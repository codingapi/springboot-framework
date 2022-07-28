package com.codingapi.springboot.leaf;

import com.codingapi.springboot.leaf.properties.LeafProperties;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "codingapi.leaf")
    public LeafProperties leafProperties(){
        return new LeafProperties();
    }

    @Bean
    public IDAllocDao allocDao(LeafProperties leafProperties){
        return new IDAllocDaoImpl(leafProperties.getJdbcUrl());
    }


    @Bean
    public IDGen idGen(IDAllocDao allocDao){
        return new SegmentIDGenImpl(allocDao);
    }

    @Bean(initMethod = "init")
    public Leaf leafClient(IDGen idGen, IDAllocDao allocDao){
        return new Leaf(idGen,allocDao);
    }

}
