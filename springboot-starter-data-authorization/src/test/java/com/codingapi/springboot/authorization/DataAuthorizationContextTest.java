package com.codingapi.springboot.authorization;

import com.codingapi.springboot.authorization.current.CurrentUser;
import com.codingapi.springboot.authorization.entity.Depart;
import com.codingapi.springboot.authorization.entity.Unit;
import com.codingapi.springboot.authorization.entity.User;
import com.codingapi.springboot.authorization.filter.DefaultDataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.interceptor.SQLRunningContext;
import com.codingapi.springboot.authorization.mask.ColumnMaskContext;
import com.codingapi.springboot.authorization.mask.impl.BankCardMask;
import com.codingapi.springboot.authorization.mask.impl.IDCardMask;
import com.codingapi.springboot.authorization.mask.impl.PhoneMask;
import com.codingapi.springboot.authorization.repository.DepartRepository;
import com.codingapi.springboot.authorization.repository.UnitRepository;
import com.codingapi.springboot.authorization.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(value = false)
public class DataAuthorizationContextTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartRepository departRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void test1() {

        unitRepository.deleteAll();
        departRepository.deleteAll();
        userRepository.deleteAll();


        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();

        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {

            @Override
            public boolean supportRowAuthorization(String tableName, String tableAlias) {
                User user = CurrentUser.getInstance().getUser();
                // 模拟仅当用户为lorne时，才进行行级过滤
                return user.getName().equalsIgnoreCase("bob");
            }

            @Override
            public Condition rowAuthorization(String tableName, String tableAlias) {
                if (tableName.equalsIgnoreCase("t_unit")) {
                    long unitId = CurrentUser.getInstance().getUser().getUnitId();
                    String conditionTemplate = "%s.id = " + unitId;
                    return Condition.formatCondition(conditionTemplate, tableAlias);
                }
                if (tableName.equalsIgnoreCase("t_depart")) {
                    long departId = CurrentUser.getInstance().getUser().getDepartId();
                    String conditionTemplate = "%s.id = " + departId;

                    // 在条件处理的过程中，执行的查询都将不会被拦截
                    List<Depart> departs = departRepository.findAll();
                    log.info("departs:{}", departs);
                    assertEquals(2, departs.size());

                    return Condition.formatCondition(conditionTemplate, tableAlias);
                }
                if (tableName.equalsIgnoreCase("t_user")) {
                    long departId = CurrentUser.getInstance().getUser().getDepartId();
                    String conditionTemplate = "%s.depart_id = " + departId;
                    return Condition.formatCondition(conditionTemplate, tableAlias);
                }
                return null;
            }

        });

        Unit rootUnit = new Unit("Coding总公司");
        unitRepository.save(rootUnit);

        Unit sdUnit = new Unit("Coding山东分公司", rootUnit.getId());
        unitRepository.save(sdUnit);

        Depart jgbDepart = new Depart("Coding架构部", rootUnit.getId());
        departRepository.save(jgbDepart);

        Depart xmbDepart = new Depart("Coding项目部", sdUnit.getId());
        departRepository.save(xmbDepart);


        User lorne = new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", jgbDepart);
        User bob = new User("bob", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);
        User tom = new User("tom", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);

        userRepository.save(lorne);
        userRepository.save(bob);
        userRepository.save(tom);


        CurrentUser.getInstance().setUser(bob);


        PageRequest request = PageRequest.of(0, 100);
        Page<User> users = userRepository.findAll(request);


        System.out.println(users.getTotalElements());
        users.forEach(System.out::println);


        assertEquals(2, users.getTotalElements());
        assertEquals(2, userRepository.count());
        assertEquals(1, departRepository.count());
        assertEquals(1, unitRepository.count());

    }


    @Test
    @Order(2)
    void test2() {

        unitRepository.deleteAll();
        departRepository.deleteAll();
        userRepository.deleteAll();


        ColumnMaskContext.getInstance().addColumnMask(new IDCardMask());
        ColumnMaskContext.getInstance().addColumnMask(new PhoneMask());
        ColumnMaskContext.getInstance().addColumnMask(new BankCardMask());

        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();

        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {
            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                return ColumnMaskContext.getInstance().mask(value);
            }

            @Override
            public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
                User user = CurrentUser.getInstance().getUser();
                return user != null && user.getName().equalsIgnoreCase("bob");
            }

        });

        Unit rootUnit = new Unit("Coding总公司");
        unitRepository.save(rootUnit);

        Unit sdUnit = new Unit("Coding山东分公司", rootUnit.getId());
        unitRepository.save(sdUnit);

        Depart jgbDepart = new Depart("Coding架构部", rootUnit.getId());
        departRepository.save(jgbDepart);

        Depart xmbDepart = new Depart("Coding项目部", sdUnit.getId());
        departRepository.save(xmbDepart);


        User lorne = new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", jgbDepart);
        User bob = new User("bob", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);
        User tom = new User("tom", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);

        userRepository.save(lorne);
        userRepository.save(bob);
        userRepository.save(tom);

        assertTrue(SQLRunningContext.getInstance().skipDataAuthorization(() -> userRepository.findAll()).size() >= 3);

        CurrentUser.getInstance().setUser(bob);

        PageRequest request = PageRequest.of(0, 100);
        Page<User> users = userRepository.findAll(request);
        assertTrue(users.getTotalElements() >= 3);

        for (User user : users) {
            assertEquals("138****5678", user.getPhone());
        }

        CurrentUser.getInstance().setUser(lorne);

        users = userRepository.findAll(request);
        assertTrue(users.getTotalElements() >= 3);

        for (User user : users) {
            assertEquals("13812345678", user.getPhone());
        }


    }


    @Test
    @Order(3)
    void test3() {

        unitRepository.deleteAll();
        departRepository.deleteAll();
        userRepository.deleteAll();

        ColumnMaskContext.getInstance().addColumnMask(new IDCardMask());
        ColumnMaskContext.getInstance().addColumnMask(new PhoneMask());
        ColumnMaskContext.getInstance().addColumnMask(new BankCardMask());

        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();

        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {
            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                return ColumnMaskContext.getInstance().mask(value);
            }

            @Override
            public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
                return true;
            }

        });

        Unit rootUnit = new Unit("Coding总公司");
        unitRepository.save(rootUnit);

        Unit sdUnit = new Unit("Coding山东分公司", rootUnit.getId());
        unitRepository.save(sdUnit);

        Depart jgbDepart = new Depart("Coding架构部", rootUnit.getId());
        departRepository.save(jgbDepart);

        Depart xmbDepart = new Depart("Coding项目部", sdUnit.getId());
        departRepository.save(xmbDepart);

        User lorne = new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", jgbDepart);
        User bob = new User("bob", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);
        User tom = new User("tom", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);

        userRepository.save(lorne);
        userRepository.save(bob);
        userRepository.save(tom);

        List<Map<String, Object>> users = jdbcTemplate.queryForList("select * from t_user");
        System.out.println(users);
        assertEquals(3, users.size());

        for (Map<String, Object> user : users) {
            assertEquals("138****5678", user.get("phone"));
        }

    }


    @Test
    @Order(4)
    void test4() throws Exception {

        unitRepository.deleteAll();
        departRepository.deleteAll();
        userRepository.deleteAll();

        Unit rootUnit = new Unit("Coding总公司");
        unitRepository.save(rootUnit);

        Unit sdUnit = new Unit("Coding山东分公司", rootUnit.getId());
        unitRepository.save(sdUnit);

        Depart jgbDepart = new Depart("Coding架构部", rootUnit.getId());
        departRepository.save(jgbDepart);

        Depart xmbDepart = new Depart("Coding项目部", sdUnit.getId());
        departRepository.save(xmbDepart);

        User lorne = new User("lorne", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", jgbDepart);
        User bob = new User("bob", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);
        User tom = new User("tom", LocalDate.parse("1991-01-01"), "beijing", "110105199003078999", "13812345678", xmbDepart);

        userRepository.save(lorne);
        userRepository.save(bob);
        userRepository.save(tom);

        String sql = "select * from t_user where phone like '%1%' and id > 1 and depart_id in (select id from t_depart where id > 0)";


        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {
            @Override
            public Condition rowAuthorization(String tableName, String tableAlias) {
                String conditionTemplate = "%s.id > -100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }

            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                System.out.println("tableName:" + tableName + ",columnName:" + columnName + ",value:" + value);
                return value;
            }

            @Override
            public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
                if ("t_depart".equalsIgnoreCase(tableName)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean supportRowAuthorization(String tableName, String tableAlias) {
                if ("t_depart".equalsIgnoreCase(tableName)) {
                    return true;
                }
                return false;
            }
        });

        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
        System.out.println(data);
    }


}
