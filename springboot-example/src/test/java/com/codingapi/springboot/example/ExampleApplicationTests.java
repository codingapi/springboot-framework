package com.codingapi.springboot.example;

import com.codingapi.springboot.example.domain.Demo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleApplicationTests {


	@Test
	void contextLoads() {
		Demo demo = new Demo("xiaoming");
		demo.changeName("xiaomi");
	}

}
