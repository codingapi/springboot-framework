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
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
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
import java.util.HashMap;
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


//    @Test
    @Order(4)
    void test4() throws Exception{
        String sql = "SELECT\n" +
                "\tt.* \n" +
                "FROM\n" +
                "\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tUNYiV.id AS '历史工作经历编号',\n" +
                "\t\t\tUNYiV.company_name AS '历史工作单位',\n" +
                "\t\t\tUNYiV.depart_name AS '历史工作部门',\n" +
                "\t\t\tUNYiV.post_name AS '历史工作岗位',\n" +
                "\t\t\tUNYiV.start_date AS '开始时间',\n" +
                "\t\t\tUNYiV.end_date AS '结束时间',\n" +
                "\t\t\towasH.员工编号 AS '员工编号',\n" +
                "\t\t\towasH.员工姓名 AS '员工姓名',\n" +
                "\t\t\towasH.员工生日 AS '员工生日',\n" +
                "\t\t\towasH.员工地址 AS '员工地址',\n" +
                "\t\t\towasH.身份证号码 AS '身份证号码',\n" +
                "\t\t\towasH.手机号 AS '手机号',\n" +
                "\t\t\towasH.部门编号 AS '部门编号',\n" +
                "\t\t\towasH.岗位编号 AS '岗位编号',\n" +
                "\t\t\towasH.任现职编号 AS '任现职编号',\n" +
                "\t\t\towasH.社团编号 AS '社团编号',\n" +
                "\t\t\towasH.社团名称 AS '社团名称',\n" +
                "\t\t\towasH.创建时间 AS '创建时间' \n" +
                "\t\tFROM\n" +
                "\t\t\tt_work AS pehMS,\n" +
                "\t\t\tt_employee AS OGwG7,\n" +
                "\t\t\tt_work_history AS UNYiV,\n" +
                "\t\t\t(\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t\tWXJj8.id AS '员工编号',\n" +
                "\t\t\t\t\tWXJj8.NAME AS '员工姓名',\n" +
                "\t\t\t\t\tWXJj8.birth_date AS '员工生日',\n" +
                "\t\t\t\t\tWXJj8.address AS '员工地址',\n" +
                "\t\t\t\t\tWXJj8.id_card AS '身份证号码',\n" +
                "\t\t\t\t\tWXJj8.phone AS '手机号',\n" +
                "\t\t\t\t\tWXJj8.depart_id AS '部门编号',\n" +
                "\t\t\t\t\tWXJj8.post_id AS '岗位编号',\n" +
                "\t\t\t\t\tWXJj8.work_id AS '任现职编号',\n" +
                "\t\t\t\t\trnGD4.id AS '社团编号',\n" +
                "\t\t\t\t\trnGD4.NAME AS '社团名称',\n" +
                "\t\t\t\t\trnGD4.create_date AS '创建时间' \n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t\tt_employee AS WXJj8,\n" +
                "\t\t\t\t\tt_league_employee AS dEj96,\n" +
                "\t\t\t\t\tt_league AS rnGD4 \n" +
                "\t\t\t\tWHERE\n" +
                "\t\t\t\t\trnGD4.id < 100 \n" +
                "\t\t\t\t\tAND dEj96.employee_id = WXJj8.id \n" +
                "\t\t\t\t\tAND dEj96.league_id = rnGD4.id \n" +
                "\t\t\t\t\tAND 1 = 1 \n" +
                "\t\t\t) AS owasH \n" +
                "\t\tWHERE\n" +
                "\t\t\tUNYiV.employee_id = OGwG7.id \n" +
                "\t\t\tAND OGwG7.work_id = pehMS.id \n" +
                "\t\t\tAND owasH.任现职编号 = pehMS.id \n" +
                "\t\t\tAND 1 = 1 \n" +
                "\t) AS t , t_employee AS e where t.员工编号 = e.id and e.id = 1";


        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DefaultDataAuthorizationFilter() {
            @Override
            public Condition rowAuthorization(String tableName, String tableAlias) {
                return super.rowAuthorization(tableName, tableAlias);
            }

            @Override
            public <T> T columnAuthorization(String tableName, String columnName, T value) {
                System.out.println("tableName:" + tableName + ",columnName:" + columnName + ",value:" + value);
                return value;
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


        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
//        System.out.println(data);
    }




}
