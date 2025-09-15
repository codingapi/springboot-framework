package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.repository.DemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JdbcQueryTest {

    @Autowired
    private DemoRepository demoRepository;


    @Test
    void test() {
        demoRepository.deleteAll();
        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);
        assertTrue(demo.getId() > 0);

        String sql = "select d.* from t_demo as d where d.id in (d.id,?,?)";
        String countSql = "select count(d.id) from t_demo as d where d.id in (d.id,?,?)";
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Integer> params = List.of(demo.getId(), demo.getId());
        Page<Map<String, Object>> page = demoRepository.dynamicNativeMapPageMapQuery(
                sql, countSql, pageRequest, params.toArray()
        );
        assertEquals(page.getTotalElements(), page.getContent().size());
    }

}
