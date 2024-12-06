package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SelectSQLAnalyzerTest {


    @Test
    void test1() throws SQLException {
        String sql = "select t1.*,t2.* from (SELECT * FROM t_employee as a2 WHERE id = 100 ) t1 ," +
                " (SELECT * FROM t_employee as a1 ) t2 ," +
                " (select * from t_employee a3 left join t_unit u on a3.unit_id = u.id ) t3 ," +
                " (select 1 =1 ) as t4 " +
                " limit 100";

        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_employee")) {
                String conditionTemplate = "%s.id > 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };

        SelectSQLAnalyzer builder = new SelectSQLAnalyzer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        assertEquals(
                "SELECT t1.*, t2.* FROM (SELECT * FROM t_employee AS a2 WHERE a2.id > 100 AND id = 100) t1," +
                        " (SELECT * FROM t_employee AS a1 WHERE a1.id > 100) t2, " +
                        "(SELECT * FROM t_employee a3 LEFT JOIN t_unit u ON a3.unit_id = u.id WHERE a3.id > 100) t3, " +
                        "(SELECT 1 = 1) AS t4 LIMIT 100", builder.getNewSQL());
    }

    @Test
    void test2() throws SQLException {
        String sql = "select e1_0.id,e1_0.address,e1_0.birth_date,e1_0.depart_id,e1_0.id_card,e1_0.name,e1_0.phone,e1_0.post_id,e1_0.work_id from t_employee e1_0 limit ?,?";

        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_employee")) {
                String conditionTemplate = "%s.id > 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };

        SelectSQLAnalyzer builder = new SelectSQLAnalyzer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        assertEquals("SELECT e1_0.id, e1_0.address, e1_0.birth_date, e1_0.depart_id, e1_0.id_card, e1_0.name, e1_0.phone, e1_0.post_id, e1_0.work_id FROM t_employee e1_0 WHERE e1_0.id > 100 LIMIT ?, ?", builder.getNewSQL());
    }
}
