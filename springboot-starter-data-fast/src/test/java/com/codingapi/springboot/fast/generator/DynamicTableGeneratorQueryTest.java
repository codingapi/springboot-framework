package com.codingapi.springboot.fast.generator;

import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQueryContext;
import com.codingapi.springboot.fast.jpa.JPAQuery;
import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import com.codingapi.springboot.fast.metadata.TableEntityMetadata;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GenerationType;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class DynamicTableGeneratorQueryTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private Environment environment;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    void generateThenUpdateQuery() throws ClassNotFoundException {
        String dialect = entityManagerFactory.getProperties().get(AvailableSettings.DIALECT).toString();
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        DynamicTableGenerator dynamicTableGenerator = new DynamicTableGenerator(Class.forName(dialect), jdbcUrl, username, password);

        TableEntityMetadata tableEntityMetadata = new TableEntityMetadata("com.codingapi.springboot.fast.entity.Test");
        tableEntityMetadata.setTable("test");
        tableEntityMetadata.addPrimaryKeyColumn(Long.class, "id", GenerationType.IDENTITY, "主键");
        tableEntityMetadata.addColumn(String.class, "name", "姓名");

        Class<?> entityClass = tableEntityMetadata.buildClass();

        // 创建表
        dynamicTableGenerator.generateMigratorTableDDL(entityClass, true);

        // 通过DynamicTableGenerator 动态创建的entity无法通过jpa的 EntityManager进行数据查询处理, 因为Entity不在JPA扫描支持的范围内。
        JPAQuery jpaQuery = JpaQueryContext.getInstance().getJPAQuery();
        assertThrows(Exception.class,()->{
            jpaQuery.listQuery(entityClass,"select t from com.codingapi.springboot.fast.entity.Test");
        });

        // 插入数据
        jdbcTemplate.update("insert into test(name) values(?)", "小明");

        // 查询数据
        JdbcQuery jdbcQuery = JdbcQueryContext.getInstance().getJdbcQuery();
        List<Map<String, Object>> list = jdbcQuery.queryForMapList("select * from test");
        System.out.println(list);
        assertFalse(list.isEmpty());

    }
}