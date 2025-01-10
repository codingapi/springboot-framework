package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.enhancer.DataPermissionSQLEnhancer;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import org.junit.jupiter.api.Order;
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

        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        String newSql = builder.getNewSQL();
        System.out.println(newSql);
        //SELECT t1.*, t2.* FROM (SELECT * FROM t_employee AS a2 WHERE a2.id > 100 AND id = 100) t1,
        // (SELECT * FROM t_employee AS a1 WHERE a1.id > 100) t2,
        // (SELECT * FROM t_employee a3 LEFT JOIN t_unit u ON a3.unit_id = u.id WHERE a3.id > 100) t3,
        // (SELECT 1 = 1) AS t4 LIMIT 100
        assertEquals(
                "SELECT t1.*, t2.* FROM (SELECT * FROM t_employee AS a2 WHERE a2.id > 100 AND id = 100) t1," +
                        " (SELECT * FROM t_employee AS a1 WHERE a1.id > 100) t2, " +
                        "(SELECT * FROM t_employee a3 LEFT JOIN t_unit u ON a3.unit_id = u.id WHERE a3.id > 100) t3, " +
                        "(SELECT 1 = 1) AS t4 LIMIT 100", newSql);
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

        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        String newSql = builder.getNewSQL();
        System.out.println(newSql);
        assertEquals("SELECT e1_0.id, e1_0.address, e1_0.birth_date, e1_0.depart_id, e1_0.id_card, e1_0.name, e1_0.phone, e1_0.post_id, e1_0.work_id FROM t_employee e1_0 WHERE e1_0.id > 100 LIMIT ?, ?", newSql);    }


    @Test
    void test3() throws SQLException {
        String sql = "select aue1_0.ba_org_code from ba03_administrative_unit aue1_0 where aue1_0.ba_org_code like (?||'__') order by aue1_0.ba_org_code desc";

        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("ba03_administrative_unit")) {
                String conditionTemplate = "%s.id > 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };

        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        String newSql = builder.getNewSQL();
        System.out.println(newSql);
        assertEquals("SELECT aue1_0.ba_org_code FROM ba03_administrative_unit aue1_0 WHERE aue1_0.id > 100 AND aue1_0.ba_org_code LIKE (? || '__') ORDER BY aue1_0.ba_org_code DESC", newSql);
    }

    @Test
    void test4() throws SQLException{
        String sql = "SELECT\n" +
                "\tUNYiV.id AS '历史工作经历编号',\n" +
                "\tUNYiV.company_name AS '历史工作单位',\n" +
                "\tUNYiV.depart_name AS '历史工作部门',\n" +
                "\tUNYiV.post_name AS '历史工作岗位',\n" +
                "\tUNYiV.start_date AS '开始时间',\n" +
                "\tUNYiV.end_date AS '结束时间',\n" +
                "\towasH.员工编号 AS '员工编号',\n" +
                "\towasH.员工姓名 AS '员工姓名',\n" +
                "\towasH.员工生日 AS '员工生日',\n" +
                "\towasH.员工地址 AS '员工地址',\n" +
                "\towasH.身份证号码 AS '身份证号码',\n" +
                "\towasH.手机号 AS '手机号',\n" +
                "\towasH.部门编号 AS '部门编号',\n" +
                "\towasH.岗位编号 AS '岗位编号',\n" +
                "\towasH.任现职编号 AS '任现职编号',\n" +
                "\towasH.社团编号 AS '社团编号',\n" +
                "\towasH.社团名称 AS '社团名称',\n" +
                "\towasH.创建时间 AS '创建时间' \n" +
                "FROM\n" +
                "\tt_work AS pehMS,\n" +
                "\tt_employee AS OGwG7,\n" +
                "\tt_work_history AS UNYiV,\n" +
                "\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tWXJj8.id AS '员工编号',\n" +
                "\t\t\tWXJj8.NAME AS '员工姓名',\n" +
                "\t\t\tWXJj8.birth_date AS '员工生日',\n" +
                "\t\t\tWXJj8.address AS '员工地址',\n" +
                "\t\t\tWXJj8.id_card AS '身份证号码',\n" +
                "\t\t\tWXJj8.phone AS '手机号',\n" +
                "\t\t\tWXJj8.depart_id AS '部门编号',\n" +
                "\t\t\tWXJj8.post_id AS '岗位编号',\n" +
                "\t\t\tWXJj8.work_id AS '任现职编号',\n" +
                "\t\t\trnGD4.id AS '社团编号',\n" +
                "\t\t\trnGD4.NAME AS '社团名称',\n" +
                "\t\t\trnGD4.create_date AS '创建时间' \n" +
                "\t\tFROM\n" +
                "\t\t\tt_employee AS WXJj8,\n" +
                "\t\t\tt_league_employee AS dEj96,\n" +
                "\t\t\tt_league AS rnGD4 \n" +
                "\t\tWHERE\n" +
                "\t\t\tdEj96.employee_id = WXJj8.id \n" +
                "\t\t\tAND dEj96.league_id = rnGD4.id \n" +
                "\t\t\tAND 1 = 1 \n" +
                "\t) AS owasH \n" +
                "WHERE\n" +
                "\tUNYiV.employee_id = OGwG7.id \n" +
                "\tAND OGwG7.work_id = pehMS.id \n" +
                "\tAND owasH.任现职编号 = pehMS.id \n" +
                "\tAND 1 = 1";


        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_league")) {
                String conditionTemplate = "%s.id < 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };

        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        System.out.println(builder.getTableAlias());;
    }

    @Test
    @Order(5)
    void test5() throws Exception{
        String sql = "SELECT next_val AS id_val FROM t_league_seq FOR UPDATE";
        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_league")) {
                String conditionTemplate = "%s.id < 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        System.out.println(builder.getTableAlias());;
    }


    @Test
    @Order(6)
    void test6() throws Exception{
        String sql = "SELECT 1=1";
        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_league")) {
                String conditionTemplate = "%s.id < 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        System.out.println(builder.getTableAlias());;
    }

    @Test
    @Order(7)
    void test7() throws Exception{
        String sql = "SELECT * from t_employee";
        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("t_employee")) {
                String conditionTemplate = "%s.id < 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        System.out.println(builder.getNewSQL());
        System.out.println(builder.getTableAlias());;
    }


    @Test
    @Order(8)
    void test8() throws Exception{
        String sql = "select ade1_0.id,ade1_0.ba_dept_name,ade1_0.ba_org_shortname,ade1_0.ba_dept_code,ade1_0.ba_code,ade1_0.ba_dept_property_code,ade1_0.ba_parent_type,ade1_0.ba_real_super_org_id,ade1_0.ba_org_is_avoidance_dept,ade1_0.ba_real_super_org_id,ade1_0.ba_super_org_name " +
                "from ba04_administrative_department ade1_0 where ade1_0.ba_parent_type=0" +
                " union all select ade2_0.id,ade2_0.ba_dept_name,ade2_0.ba_org_shortname,ade2_0.ba_dept_code,ade2_0.ba_code,ade2_0.ba_dept_property_code,ade2_0.ba_parent_type,ade2_0.ba_real_super_org_id,ade2_0.ba_org_is_avoidance_dept,ade3_0.ba_real_super_org_id,ade3_0.ba_super_org_name" +
                " from ba04_administrative_department ade2_0 left join ba04_administrative_department ade3_0 on ade2_0.ba_real_super_org_id=ade3_0.id " +
                "where ade2_0.ba_real_super_org_id=1";



        RowHandler rowHandler = (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("ba04_administrative_department")) {
                String conditionTemplate = "%s.id < 100 ";
                return Condition.formatCondition(conditionTemplate, tableAlias);
            }
            return null;
        };
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(sql, rowHandler);
        String newSQL = builder.getNewSQL();
        System.out.println(newSQL);
        System.out.println(builder.getTableAlias());
        assertEquals("SELECT ade1_0.id, ade1_0.ba_dept_name, ade1_0.ba_org_shortname, ade1_0.ba_dept_code, ade1_0.ba_code, ade1_0.ba_dept_property_code, ade1_0.ba_parent_type, ade1_0.ba_real_super_org_id, ade1_0.ba_org_is_avoidance_dept, ade1_0.ba_real_super_org_id, ade1_0.ba_super_org_name FROM ba04_administrative_department ade1_0 WHERE ade1_0.id < 100 AND ade1_0.ba_parent_type = 0 " +
                "UNION ALL SELECT ade2_0.id, ade2_0.ba_dept_name, ade2_0.ba_org_shortname, ade2_0.ba_dept_code, ade2_0.ba_code, ade2_0.ba_dept_property_code, ade2_0.ba_parent_type, ade2_0.ba_real_super_org_id, ade2_0.ba_org_is_avoidance_dept, ade3_0.ba_real_super_org_id, ade3_0.ba_super_org_name FROM ba04_administrative_department ade2_0 LEFT JOIN ba04_administrative_department ade3_0 ON ade2_0.ba_real_super_org_id = ade3_0.id WHERE ade3_0.id < 100 AND ade2_0.id < 100 AND ade2_0.ba_real_super_org_id = 1", newSQL);
    }
}
