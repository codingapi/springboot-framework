package com.codingapi.springboot.permission.analyze;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
public class SqlAnalyzerConfiguration {

    static class SqlAnalyzerFilterBeanInit {
        public SqlAnalyzerFilterBeanInit(List<SqlAnalyzerFilter> filters) {
            if(filters!=null&&filters.size()>0) {
                AnalyzerFilterContext.getInstance().addFilters(filters);
            }
        }
    }

    @Bean
    public SqlAnalyzerFilterBeanInit sqlAnalyzerFilterInit(@Autowired(required = false) List<SqlAnalyzerFilter> filters){
        return new SqlAnalyzerFilterBeanInit(filters);
    }

}
