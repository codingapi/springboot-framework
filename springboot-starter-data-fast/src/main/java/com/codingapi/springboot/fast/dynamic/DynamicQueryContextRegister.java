package com.codingapi.springboot.fast.dynamic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

@AllArgsConstructor
public class DynamicQueryContextRegister implements InitializingBean {

    private DynamicQuery dynamicQuery;

    @Override
    public void afterPropertiesSet() throws Exception {
        DynamicQueryContext.getInstance().setDynamicQuery(dynamicQuery);
    }

}
