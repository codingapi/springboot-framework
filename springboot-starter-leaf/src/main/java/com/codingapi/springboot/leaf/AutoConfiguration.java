package com.codingapi.springboot.leaf;

import com.codingapi.springboot.leaf.exception.InitException;
import com.codingapi.springboot.leaf.segment.dao.IDAllocDao;
import com.codingapi.springboot.leaf.segment.dao.impl.IDAllocDaoImpl;
import com.codingapi.springboot.leaf.service.SegmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
public class AutoConfiguration {

    @Bean
    public IDAllocDao allocDao(){
        return new IDAllocDaoImpl();
    }

    @Bean
    public SegmentService segmentService(IDAllocDao allocDao) throws InitException {
        return new SegmentService(allocDao);
    }

    @Bean(initMethod = "init")
    public LeafClient leafClient(SegmentService segmentService){
        return new LeafClient(segmentService);
    }
}
