package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.enhancer.DataPermissionSQLEnhancer;
import com.codingapi.springboot.authorization.enhancer.TableColumnAliasContext;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GitHub Issue #212 复现测试
 * <p>
 * 如下SQL无法正常的提取到表名和字段数据信息：
 * 外层为分页包装的子查询（FROM ( ... ) AS __base__），内层为多表 LEFT JOIN，
 * 且 JOIN 对象包含 ROW_NUMBER() OVER (PARTITION BY ...) 窗口函数派生表、
 * CAST、CASE WHEN、ORDER BY ... NULLS LAST 等复杂表达式。
 */
class Issue212SQLTest {

    private static final String SQL = """
            SELECT
              id,
              unit_name,
              department_name,
              employee_name,
              sex,
              former_name,
              nationality_code,
              person_nation_code,
              card_type_code,
              card_no,
              card_birth_date,
              present_address,
              bron_place_desc,
              native_place_desc,
              household_type_code,
              household_local,
              political_affiliation_code,
              join_date,
              join_org_name,
              introducer,
              health_status_code,
              marital_status_code,
              whether_disabled,
              staff_speciality,
              sap_number,
              remarks,
              telephone,
              email,
              emergency_contact,
              emergency_contact_relation_code,
              emergency_contact_phone,
              archive_birth_date,
              orig_policy_retire_age_code,
              is_use_new_policy_retire_age,
              new_policy_retire_age,
              start_work_date,
              industry_entry_date,
              entry_date,
              company_entry_date,
              industry_entry_type_code,
              company_entry_type_code,
              archival_custodian_code,
              unit_center_record,
              labor_type_code,
              employment_form_code,
              eoh_person_category_code,
              whether_register,
              contract_sign_unit,
              full_time_diploma_code,
              full_time_degree_code,
              on_job_diploma_code,
              on_job_degree_code,
              highest_technology_name,
              highest_technology_level_code,
              highest_skill_name,
              highest_skill_level_code,
              employee_code,
              person_code,
              person_status_code,
              post_type_code,
              position_hierarchy_code
            FROM
              (
                SELECT
                  emp.*,
                  org.SYSTEM_CODE AS orgCode,
                  edu.highest_diploma_code,
                  edu.highest_degree_code,
                  edu.full_time_diploma_code,
                  edu.full_time_degree_code,
                  edu.on_job_diploma_code,
                  edu.on_job_degree_code,
                  tech.highest_technology_name,
                  tech.highest_technology_level_code,
                  skill.highest_skill_name,
                  skill.highest_skill_level_code,
                  post.post_type_code AS post_type_code,
                  curpos.position_hierarchy_code AS position_hierarchy_code
                FROM
                  T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO emp
                  LEFT JOIN BIZ_PBM_ORGANIZATION org ON org.id = emp.department_id
                  LEFT JOIN (
                    SELECT
                      e.employee_id,
                      e.highest_diploma_code,
                      e.highest_degree_code,
                      e.full_time_diploma_code,
                      e.full_time_degree_code,
                      e.on_job_diploma_code,
                      e.on_job_degree_code,
                      ROW_NUMBER() OVER (
                        PARTITION BY
                          e.employee_id
                        ORDER BY
                          e.id DESC
                      ) AS rn
                    FROM
                      T_DYNAMIC_BIZ_PBM_EMP_EDUCATION_HIGHEST e
                  ) edu ON edu.employee_id = emp.id
                  AND edu.rn = 1
                  LEFT JOIN (
                    SELECT
                      t.employee_id,
                      t.highest_technology_name,
                      t.highest_technology_level_code,
                      ROW_NUMBER() OVER (
                        PARTITION BY
                          t.employee_id
                        ORDER BY
                          t.id DESC
                      ) AS rn
                    FROM
                      T_DYNAMIC_BIZ_PBM_EMP_TECHNOLOGY_HIGHEST t
                  ) tech ON tech.employee_id = emp.id
                  AND tech.rn = 1
                  LEFT JOIN (
                    SELECT
                      s.employee_id,
                      s.highest_skill_name,
                      s.highest_skill_level_code,
                      ROW_NUMBER() OVER (
                        PARTITION BY
                          s.employee_id
                        ORDER BY
                          s.id DESC
                      ) AS rn
                    FROM
                      T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST s
                  ) skill ON skill.employee_id = emp.id
                  AND skill.rn = 1
                  LEFT JOIN BIZ_PBM_POST post ON post.id = CAST(emp.post_id AS BIGINT)
                  LEFT JOIN (
                    SELECT
                      c.employee_id,
                      c.position_hierarchy_code,
                      c.current_hierarchy_date
                    FROM
                      (
                        SELECT
                          p.employee_id,
                          p.position_hierarchy_code,
                          p.current_hierarchy_date,
                          ROW_NUMBER() OVER (
                            PARTITION BY
                              p.employee_id
                            ORDER BY
                              p.current_position_date DESC NULLS LAST,
                              p.id DESC
                          ) AS rn
                        FROM
                          T_DYNAMIC_BIZ_PBM_EMP_POSITION p
                        WHERE
                          p.whether_current = 1
                      ) c
                    WHERE
                      c.rn = 1
                  ) curpos ON curpos.employee_id = emp.id
                WHERE
                  emp.sys_deleted = 0
                ORDER BY
                  CASE
                    WHEN org.SYSTEM_CODE IS NULL THEN 1
                    ELSE 0
                  END,
                  org.tree_sort,
                  CASE
                    WHEN curpos.position_hierarchy_code IS NULL THEN 1
                    ELSE 0
                  END,
                  CASE
                    WHEN curpos.position_hierarchy_code LIKE 'dict_u_w_p_layer%' THEN CAST(
                      REPLACE (
                        curpos.position_hierarchy_code,
                        'dict_u_w_p_layer_',
                        ''
                      ) AS INT
                    )
                    ELSE 999
                  END,
                  CASE
                    WHEN curpos.current_hierarchy_date IS NULL THEN 1
                    ELSE 0
                  END,
                  curpos.current_hierarchy_date,
                  emp.id
              ) AS __base__
            WHERE
              1 = 1
            LIMIT
              ?
            OFFSET
              ?
            """;

    /**
     * 对员工基础信息表注入行权限条件：%s.sys_deleted = 0 之外再追加 %s.unit_id IN (...)
     */
    private static RowHandler rowHandler() {
        return (subSql, tableName, tableAlias) -> {
            if (tableName.equalsIgnoreCase("T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO")) {
                return Condition.formatCondition("%s.unit_id = 1000", tableAlias);
            }
            return null;
        };
    }

    @Test
    void complexSQLShouldBeParsed() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        assertNotNull(builder.getTableAlias(), "表别名上下文不应为空");
    }

    @Test
    void shouldExtractAllPhysicalTables() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        builder.getNewSQL();
        TableColumnAliasContext context = builder.getTableAlias();
        System.out.println("tableAlias = " + context.getTableAlias());

        // 嵌套 LEFT JOIN 子查询中的物理表都应被提取到
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO", context.getTableName("emp"));
        assertEquals("BIZ_PBM_ORGANIZATION", context.getTableName("org"));
        assertEquals("BIZ_PBM_POST", context.getTableName("post"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_EDUCATION_HIGHEST", context.getTableName("e"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_TECHNOLOGY_HIGHEST", context.getTableName("t"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST", context.getTableName("s"));
        assertEquals("T_DYNAMIC_BIZ_PBM_EMP_POSITION", context.getTableName("p"));
    }

    @Test
    void shouldInjectPermissionConditionIntoNestedTable() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        String newSql = builder.getNewSQL();
        System.out.println("newSql = " + newSql);

        // 权限条件必须被注入到内层 emp 表的 WHERE 中
        assertTrue(newSql.contains("emp.unit_id = 1000"),
                "行权限条件未注入到嵌套子查询的 emp 表，提取/增强失败");
    }

    @Test
    void shouldResolveDerivedColumnsOnIssueSQL() throws SQLException {
        DataPermissionSQLEnhancer builder = new DataPermissionSQLEnhancer(SQL, rowHandler());
        builder.getNewSQL();
        TableColumnAliasContext context = builder.getTableAlias();

        // 元数据表名为派生别名 __base__（H2 等）或空串（PG，issue 截图现场）时，
        // 裸列应能沿多层派生链归因到物理表字段：

        // emp.* 单星展开归因
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO", "card_no"},
                context.resolveTableNameAndColumn("__base__", "card_no"));
        // 二级派生链：curpos -> c -> p
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_POSITION", "position_hierarchy_code"},
                context.resolveTableNameAndColumn("", "position_hierarchy_code"));
        // 一层派生：skill 子查询
        assertArrayEquals(new String[]{"T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST", "highest_skill_name"},
                context.resolveTableNameAndColumn("__BASE__", "highest_skill_name"));
        // 显式别名投影：org.SYSTEM_CODE AS orgCode
        assertArrayEquals(new String[]{"BIZ_PBM_ORGANIZATION", "SYSTEM_CODE"},
                context.resolveTableNameAndColumn("", "orgCode"));
        // JOIN 直连表：post.post_type_code
        assertArrayEquals(new String[]{"BIZ_PBM_POST", "post_type_code"},
                context.resolveTableNameAndColumn("__base__", "post_type_code"));
        // 多义列（多个派生表均含 employee_id 且归属不同物理表）：保守回退，不误归因
        assertArrayEquals(new String[]{"", "employee_id"},
                context.resolveTableNameAndColumn("", "employee_id"));
    }
}
