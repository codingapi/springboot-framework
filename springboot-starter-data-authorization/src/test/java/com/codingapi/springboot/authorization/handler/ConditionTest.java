package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import com.codingapi.springboot.authorization.condition.JoinConditionSQL;
import com.codingapi.springboot.authorization.condition.WhereConditionSQL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Condition 单元测试
 */
class ConditionTest {

    @Test
    void testEmptyConstructor() {
        Condition condition = new Condition();
        assertNotNull(condition.getConditionList());
        assertEquals(0, condition.getConditionList().size());
    }

    @Test
    void testConditionWithWhereSql() {
        Condition condition = new Condition("t.id = 1");
        assertEquals(1, condition.getConditionList().size());
        IConditionSQL conditionSQL = condition.getConditionList().get(0);
        assertTrue(conditionSQL instanceof WhereConditionSQL);
        assertEquals("t.id = 1", ((WhereConditionSQL) conditionSQL).getCondition());
    }

    @Test
    void testAddConditionSql() {
        Condition condition = new Condition();
        condition.addConditionSQL(new WhereConditionSQL("t.id = 1"));
        condition.addConditionSQL(new JoinConditionSQL(JoinConditionSQL.Type.INNER, "t_unit", "u", "u.id = t.id"));
        assertEquals(2, condition.getConditionList().size());
    }

    @Test
    void testCustomCondition() {
        Condition condition = Condition.customCondition("t.id = 1");
        assertEquals(1, condition.getConditionList().size());
        WhereConditionSQL whereConditionSQL = (WhereConditionSQL) condition.getConditionList().get(0);
        assertEquals("t.id = 1", whereConditionSQL.getCondition());
    }

    @Test
    void testFormatCondition() {
        Condition condition = Condition.formatCondition("%s.id = %d", "u", 10);
        WhereConditionSQL whereConditionSQL = (WhereConditionSQL) condition.getConditionList().get(0);
        assertEquals("u.id = 10", whereConditionSQL.getCondition());
    }

    @Test
    void testEmptyConditionReturnsNull() {
        assertNull(Condition.emptyCondition());
    }

    @Test
    void testDefaultCondition() {
        Condition condition = Condition.defaultCondition();
        WhereConditionSQL whereConditionSQL = (WhereConditionSQL) condition.getConditionList().get(0);
        assertEquals("1=1", whereConditionSQL.getCondition());
    }
}
