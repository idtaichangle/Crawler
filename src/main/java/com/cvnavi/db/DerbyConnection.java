package com.cvnavi.db;

import com.cvnavi.base.ServletContextCleaner;
import com.cvnavi.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

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
                ServletContextCleaner.registeCloseable(con);
            }
        } catch (Exception e) {
            log.error(e);
        }

        return con;
    }
}
