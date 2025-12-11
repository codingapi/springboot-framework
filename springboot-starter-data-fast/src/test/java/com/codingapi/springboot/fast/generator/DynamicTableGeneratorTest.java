package com.codingapi.springboot.fast.generator;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.metadata.TableEntityMetadata;
import jakarta.persistence.GenerationType;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableGeneratorTest {

    @Test
    void generateTableDDL() {
        DynamicTableGenerator dynamicTableGenerator = new DynamicTableGenerator(H2Dialect.class,"jdbc:h2:file:./test.db");
        List<Exception> exceptions = dynamicTableGenerator.validatorTable(Demo.class);
        System.out.println(exceptions);

        String createDDL = dynamicTableGenerator.generateCreateTableDDL(Demo.class);
        System.out.println("createDDL:\n" + createDDL);
        assertNotNull(createDDL);

        String dropDDL = dynamicTableGenerator.generateDropTableDDL(Demo.class);
        System.out.println("dropDDL:\n" + dropDDL);
        assertNotNull(dropDDL);

        String migrateDDL = dynamicTableGenerator.generateMigratorTableDDL(Demo.class);
        System.out.println("migrateDDL:\n" + migrateDDL);
        assertNotNull(migrateDDL);
    }

    @Test
    void dynamicGenerateTableDDL() {
        DynamicTableGenerator dynamicTableGenerator = new DynamicTableGenerator(H2Dialect.class,"jdbc:h2:file:./test.db");

        TableEntityMetadata tableEntityMetadata = new TableEntityMetadata("com.codingapi.entity.Test");
        tableEntityMetadata.setTable("test");
        tableEntityMetadata.addPrimaryKeyColumn(Long.class,"id", GenerationType.IDENTITY,"主键");
        tableEntityMetadata.addColumn(String.class,"name","姓名");

        Class<?> entityClass = tableEntityMetadata.buildClass();

        List<Exception> exceptions = dynamicTableGenerator.validatorTable(entityClass);
        System.out.println(exceptions);

        String createDDL = dynamicTableGenerator.generateCreateTableDDL(entityClass);
        System.out.println("createDDL:\n" + createDDL);
        assertNotNull(createDDL);

        String dropDDL = dynamicTableGenerator.generateDropTableDDL(entityClass);
        System.out.println("dropDDL:\n" + dropDDL);
        assertNotNull(dropDDL);

        String migrateDDL = dynamicTableGenerator.generateMigratorTableDDL(entityClass);
        System.out.println("migrateDDL:\n" + migrateDDL);
        assertNotNull(migrateDDL);

    }
}