package com.codingapi.springboot.data.permission.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author lorne
 * @since 1.0.0
 */
public class MyDriver implements Driver {

    private final static String DRIVER_FLAG="my-driver:";

    private static Driver instance = new MyDriver();

    static {
        try {
            DriverManager.registerDriver(instance);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not register P6SpyDriver with DriverManager", e);
        }
    }

    List<Driver> registeredDrivers() {
        List<Driver> result = new ArrayList<Driver>();
        for (Enumeration<Driver> driverEnumeration = DriverManager.getDrivers(); driverEnumeration.hasMoreElements(); ) {
            result.add(driverEnumeration.nextElement());
        }
        return result;
    }

    private String extractRealUrl(String url) {
        return acceptsURL(url) ? url.replace(DRIVER_FLAG, "") : url;
    }

    protected Driver findDriverByUrl(String url) throws SQLException {
        String realUrl = extractRealUrl(url);
        Driver driver = null;
        for (Driver item: registeredDrivers() ) {
            try {
                if (item.acceptsURL(extractRealUrl(url))) {
                    driver = item;
                    break;
                }
            } catch (SQLException e) {
            }
        }
        if( driver == null ) {
            throw new SQLException("Unable to find a driver that accepts " + realUrl);
        }
        return driver;
    }

    @Override
    public Connection connect(String url, Properties properties) throws SQLException {
        if (url == null) {
            throw new SQLException("url is required");
        }

        if( !acceptsURL(url) ) {
            return null;
        }

        // find the real driver for the URL
        Driver passThru = findDriverByUrl(url);

        final Connection conn;

        try {
            conn =  passThru.connect(extractRealUrl(url), properties);

        } catch (SQLException e) {

            throw e;
        }

        return new MyConnection(conn);
    }

    @Override
    public boolean acceptsURL(String url){
        return url != null && url.startsWith("jdbc:"+DRIVER_FLAG);
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties) throws SQLException {
        return findDriverByUrl(url).getPropertyInfo(url, properties);
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

}
