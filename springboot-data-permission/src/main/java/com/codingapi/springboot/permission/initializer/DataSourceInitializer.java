package com.codingapi.springboot.permission.initializer;

import com.codingapi.springboot.permission.db.DBTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class DataSourceInitializer {

    private final DataSource dataSource;
    private final QueryRunner queryRunner;

    public DataSourceInitializer(DataSource dataSource) throws SQLException{
        this.dataSource = dataSource;
        this.queryRunner = new QueryRunner(dataSource);
        this.init();
    }

    private void init() throws SQLException {
        DBTable dbTable = new DBTable(dataSource.getConnection());
        List<String> tableNames =  dbTable.tableNames();
        log.info("tableNames :{}",tableNames);
    }

}
