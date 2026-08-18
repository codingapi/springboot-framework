package com.codingapi.springboot.authorization.jdbc;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.jdbc.proxy.ConnectionProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AuthorizationJdbcDriver 单元测试
 * 验证代理驱动的查找、包装与元信息行为（使用 H2 内存库，无外网依赖）
 */
class AuthorizationJdbcDriverTest {

    private AuthorizationJdbcDriver driver;

    @BeforeAll
    static void loadH2Driver() throws ClassNotFoundException {
        // 确保 H2 驱动已注册到 DriverManager
        Class.forName("org.h2.Driver");
    }

    @BeforeEach
    void setUp() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
        driver = new AuthorizationJdbcDriver();
    }

    @AfterEach
    void tearDown() {
        DataAuthorizationContext.getInstance().clearDataAuthorizationFilters();
    }

    @Test
    void testAcceptsUrl() throws SQLException {
        assertEquals(false, driver.acceptsURL(null));
        assertTrue(driver.acceptsURL("jdbc:h2:mem:acceptsUrlTest"));
        assertEquals(false, driver.acceptsURL("jdbc:not-exists:xyz"));
    }

    @Test
    void testConnectWithNullUrl() {
        SQLException exception = assertThrows(SQLException.class, () -> driver.connect(null, new Properties()));
        assertTrue(exception.getMessage().contains("URL cannot be null"));
    }

    @Test
    void testConnectWithNoSuitableDriver() {
        SQLException exception = assertThrows(SQLException.class,
                () -> driver.connect("jdbc:not-exists:xyz", new Properties()));
        assertTrue(exception.getMessage().contains("No suitable driver"));
    }

    @Test
    void testConnectReturnsConnectionProxyAndWorks() throws SQLException {
        String url = "jdbc:h2:mem:authorizationDriverTest";
        Connection connection = driver.connect(url, new Properties());
        try {
            assertNotNull(connection);
            assertTrue(connection instanceof ConnectionProxy);

            // 第二次使用相同 URL，命中驱动缓存分支
            Connection cachedConnection = driver.connect(url, new Properties());
            assertTrue(cachedConnection instanceof ConnectionProxy);
            cachedConnection.close();

            // 通过代理连接执行真实查询
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1");
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
            resultSet.close();
            statement.close();
        } finally {
            connection.close();
        }
    }

    @Test
    void testGetPropertyInfo() throws SQLException {
        assertNotNull(driver.getPropertyInfo("jdbc:h2:mem:propertyInfoTest", new Properties()));
        assertThrows(SQLException.class, () -> driver.getPropertyInfo("jdbc:not-exists:xyz", new Properties()));
    }

    @Test
    void testDriverMetaInfo() throws SQLException {
        assertEquals(1, driver.getMajorVersion());
        assertEquals(0, driver.getMinorVersion());
        assertEquals(false, driver.jdbcCompliant());
        Logger parentLogger = driver.getParentLogger();
        assertNotNull(parentLogger);
    }

    @Test
    void testDriverRegisteredInDriverManager() {
        boolean found = false;
        java.util.Enumeration<Driver> drivers = java.sql.DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver candidate = drivers.nextElement();
            if (candidate instanceof AuthorizationJdbcDriver) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
