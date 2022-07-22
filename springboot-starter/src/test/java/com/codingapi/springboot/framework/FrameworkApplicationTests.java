package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.domain.Demo;
import com.codingapi.springboot.framework.event.SpringEventConfiguration;
import com.codingapi.springboot.framework.handler.DemoConfiguration;
import com.codingapi.springboot.framework.handler.HandlerBeanDefinitionRegistrar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SpringEventConfiguration.class, HandlerBeanDefinitionRegistrar.class,DemoConfiguration.class})
class FrameworkApplicationTests {


	@Test
	void contextLoads() {
		Demo demo = new Demo("123");
		demo.changeName("234");
	}

}
