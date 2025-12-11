package com.codingapi.springboot.fast.dynamic;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.metadata.EntityMetaData;
import jakarta.persistence.GenerationType;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicEntityBuilderTest {

    @Test
    void generateTableDDL() {
        DynamicEntityBuilder dynamicEntityBuilder = new DynamicEntityBuilder(H2Dialect.class,"jdbc:h2:file:./test.db");
        List<Exception> exceptions = dynamicEntityBuilder.validatorTable(Demo.class);
        System.out.println(exceptions);
        assertTrue(exceptions.isEmpty());

        String createDDL = dynamicEntityBuilder.generateCreateTableDDL(Demo.class);
        System.out.println("createDDL:\n" + createDDL);
        assertNotNull(createDDL);

        String dropDDL = dynamicEntityBuilder.generateDropTableDDL(Demo.class);
        System.out.println("dropDDL:\n" + dropDDL);
        assertNotNull(dropDDL);

        String migrateDDL = dynamicEntityBuilder.generateMigratorTableDDL(Demo.class);
        System.out.println("migrateDDL:\n" + migrateDDL);
        assertNotNull(migrateDDL);
    }

    @Test
    void dynamicGenerateTableDDL() {
        DynamicEntityBuilder dynamicEntityBuilder = new DynamicEntityBuilder(H2Dialect.class,"jdbc:h2:file:./test.db");

        EntityMetaData entityMetaData = new EntityMetaData("com.codingapi.entity.Test");
        entityMetaData.setTable("test");
        entityMetaData.addPrimaryKeyColumn(Long.class,"id", GenerationType.IDENTITY,"主键");
        entityMetaData.addColumn(String.class,"name","姓名");

        Class<?> entityClass = entityMetaData.buildClass();

        List<Exception> exceptions = dynamicEntityBuilder.validatorTable(entityClass);
        System.out.println(exceptions);
        assertFalse(exceptions.isEmpty());

        String createDDL = dynamicEntityBuilder.generateCreateTableDDL(entityClass);
        System.out.println("createDDL:\n" + createDDL);
        assertNotNull(createDDL);

        String dropDDL = dynamicEntityBuilder.generateDropTableDDL(entityClass);
        System.out.println("dropDDL:\n" + dropDDL);
        assertNotNull(dropDDL);

        String migrateDDL = dynamicEntityBuilder.generateMigratorTableDDL(entityClass);
        System.out.println("migrateDDL:\n" + migrateDDL);
        assertNotNull(migrateDDL);

    }
}