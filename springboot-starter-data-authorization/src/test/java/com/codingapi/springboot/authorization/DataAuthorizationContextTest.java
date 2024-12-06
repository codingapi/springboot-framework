package com.codingapi.springboot.authorization;

import com.codingapi.springboot.authorization.entity.User;
import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.mask.ColumnMaskContext;
import com.codingapi.springboot.authorization.mask.impl.BankCardMask;
import com.codingapi.springboot.authorization.mask.impl.IDCardMask;
import com.codingapi.springboot.authorization.mask.impl.PhoneMask;
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
public class DataAuthorizationContextTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void test1() {

        ColumnMaskContext.getInstance().addColumnMask(new IDCardMask());
        ColumnMaskContext.getInstance().addColumnMask(new PhoneMask());
        ColumnMaskContext.getInstance().addColumnMask(new BankCardMask());

        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DataAuthorizationFilter() {
            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                return ColumnMaskContext.getInstance().mask(value);
            }

            @Override
            public Condition rowAuthorization(String tableName, String tableAlias) {
                if (tableName.equalsIgnoreCase("t_user")) {
                    String conditionTemplate = "%s.id > 1 ";
                    return Condition.formatCondition(conditionTemplate, tableAlias);
                }
                return null;
            }

            @Override
            public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
                return true;
            }

            @Override
            public boolean supportRowAuthorization(String tableName, String tableAlias) {
                return true;
            }
        });


        userRepository.save(new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678"));
        userRepository.save(new User("bob", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678"));
        userRepository.save(new User("tom", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678"));


        PageRequest request = PageRequest.of(0, 100);
        Page<User> users = userRepository.findAll(request);


        System.out.println(users.getTotalElements());
        users.forEach(System.out::println);


        assertEquals(2, users.getTotalElements());
        assertEquals(2, userRepository.count());

    }
}
