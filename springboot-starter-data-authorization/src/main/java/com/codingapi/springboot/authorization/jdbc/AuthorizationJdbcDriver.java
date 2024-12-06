package com.codingapi.springboot.authorization.jdbc;


import com.codingapi.springboot.authorization.jdbc.proxy.ConnectionProxy;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public class AuthorizationJdbcDriver implements Driver {

    private Driver driver;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return new ConnectionProxy(driver.connect(url, info));
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.acceptsURL(url)) {
                this.driver = driver;
                return true;
            }
        }
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
    }
}
