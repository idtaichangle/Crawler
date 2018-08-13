package com.cvnavi.db;

import com.cvnavi.config.Config;
import com.cvnavi.web.WebContextCleanup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DerbyConnection extends DBConnection {

    static Logger log= LogManager.getLogger(DerbyConnection.class);
    protected static Connection con;
    /**
     * 获取数据库连接。使用完毕后，可以不用关闭连接。web app销毁时会关闭连接。
     * @return
     */
    @Override
    public Connection get() {
        try {
            if (con == null || con.isClosed()) {
                System.setProperty("derby.system.home", System.getProperty("user.home")+ File.separator+".derby");
                Class.forName(Config.dbDriver);
                con = DriverManager.getConnection(Config.dbUrl, Config.dbUser, Config.dbPassword);
                WebContextCleanup.registeCloseable(con);
            }
        } catch (Exception e) {
            log.error(e);
        }

        return con;
    }

    @Override
    public void close() {
        try {
            DriverManager.getConnection(Config.dbUrl+";shutdown=true");
//            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            log.error(e);
        }

        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
                log.info(String.format("deregistering jdbc driver: %s", driver));
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
