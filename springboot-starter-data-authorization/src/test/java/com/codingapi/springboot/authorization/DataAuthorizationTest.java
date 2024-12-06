package com.codingapi.springboot.authorization;

import com.codingapi.springboot.authorization.entity.User;
import com.codingapi.springboot.authorization.handler.ColumnHandlerContext;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.DefaultColumnHandler;
import com.codingapi.springboot.authorization.handler.RowHandlerContext;
import com.codingapi.springboot.authorization.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class DataAuthorizationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void test1() {

        ColumnHandlerContext.getInstance().setColumnHandler(new DefaultColumnHandler() {
            @Override
            public String getString(int columnIndex, String tableName, String columnName, String value) {
                log.info("columnIndex:{},tableName:{},columnName:{},value:{}", columnIndex, tableName, columnName, value);
                return "***";
            }
        });

        RowHandlerContext.getInstance().setRowHandler((subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_user")) {
                String conditionTemplate = "%s.id > 1 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        });

        userRepository.save(new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "123456", "123456"));
        userRepository.save(new User("bob", LocalDate.parse("1991-01-01"), "beijing", "123456", "123456"));
        userRepository.save(new User("tom", LocalDate.parse("1991-01-01"), "beijing", "123456", "123456"));


        PageRequest request = PageRequest.of(0, 100);
        Page<User> users = userRepository.findAll(request);


        System.out.println(users.getTotalElements());
        users.forEach(System.out::println);


        assertEquals(2, users.getTotalElements());
        assertEquals(2, userRepository.count());

    }
}
