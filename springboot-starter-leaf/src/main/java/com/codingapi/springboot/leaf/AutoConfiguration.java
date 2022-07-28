package com.codingapi.springboot.leaf;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IDAllocDao allocDao(){
        return new IDAllocDaoImpl("jdbc:h2:mem:leaf;DB_CLOSE_DELAY=-1");
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
