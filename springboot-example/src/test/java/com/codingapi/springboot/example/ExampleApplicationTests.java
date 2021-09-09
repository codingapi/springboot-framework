package com.codingapi.springboot.example;

import com.codingapi.springboot.example.domain.Demo;
import com.codingapi.springboot.example.repository.DemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleApplicationTests {

	@Autowired
	private DemoRepository demoRepository;

	@Test
	void contextLoads() {
		Demo demo = new Demo("xiaoming");
		demo.changeName("xiaomi");

		demoRepository.save(demo);
	}

}
