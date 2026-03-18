package com.codingapi.springboot.authorization.jdbc;


import com.codingapi.springboot.authorization.jdbc.proxy.ConnectionProxy;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class AuthorizationJdbcDriver implements Driver {

    /**
     * Cache driver by URL to avoid repeated traversal.
     * Thread-safe since multiple connections may be created concurrently.
     */
    private final ConcurrentHashMap<String, Driver> driverCache = new ConcurrentHashMap<>();

    /**
     * Cached reference to any non-self driver (for methods without URL).
     */
    private volatile Driver anyDriverCache;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Driver targetDriver = getTargetDriver(url);
        if (targetDriver == null) {
            return null;
        }
        return new ConnectionProxy(targetDriver.connect(url, info));
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return getTargetDriver(url) != null;
    }

    /**
     * Get target driver for the given URL.
     * Uses cached result if available, otherwise finds and caches.
     */
    private Driver getTargetDriver(String url) throws SQLException {
        // Fast path: check cache first
        Driver cached = driverCache.get(url);
        if (cached != null) {
            return cached;
        }

        // Cache miss: find and cache
        Driver found = findDriver(url);
        if (found != null) {
            driverCache.putIfAbsent(url, found);
        }
        return found;
    }

    /**
     * Find the target driver that accepts the URL from DriverManager.
     */
    private Driver findDriver(String url) throws SQLException {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver != this && driver.acceptsURL(url)) {
                return driver;
            }
        }
        return null;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        Driver driver = getTargetDriver(url);
        if (driver != null) {
            return driver.getPropertyInfo(url, info);
        }
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return getAnyDriver().getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return getAnyDriver().getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        Driver driver = getAnyDriver();
        return driver != null && driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getAnyDriver().getParentLogger();
    }

    /**
     * Get any registered driver other than this one.
     * Caches the first found result since driver set doesn't change at runtime.
     */
    private Driver getAnyDriver() {
        // Double-checked locking for lazy initialization
        Driver cached = anyDriverCache;
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            cached = anyDriverCache;
            if (cached != null) {
                return cached;
            }
            anyDriverCache = findAnyDriver();
            return anyDriverCache;
        }
    }

    /**
     * Find any registered driver other than this one.
     * Used for methods that don't have URL available.
     */
    private Driver findAnyDriver() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver != this) {
                return driver;
            }
        }
        throw new IllegalStateException("No JDBC driver found");
    }
}
