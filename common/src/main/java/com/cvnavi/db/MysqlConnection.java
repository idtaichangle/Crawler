package com.cvnavi.db;

import com.cvnavi.config.Config;
import com.cvnavi.web.WebContextCleanup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlConnection extends DBConnection{

    static Logger log= LogManager.getLogger(MysqlConnection.class);
    protected static Connection con;
    /**
     * 获取数据库连接。使用完毕后，可以不用关闭连接。web app销毁时会关闭连接。
     * @return
     */
    @Override
    public Connection get() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName(Config.dbDriver);
                con = DriverManager.getConnection(Config.dbUrl, Config.dbUser, Config.dbPassword);
                WebContextCleanup.registeCloseable(con);
            }
        } catch (Exception e) {
            log.error(e);
            if(e.getMessage().contains("Unknown database")){
                try {
                    createDatabase();
                    con = DriverManager.getConnection(Config.dbUrl, Config.dbUser, Config.dbPassword);
                    WebContextCleanup.registeCloseable(con);
                } catch (SQLException e1) {
                    log.error(e1);
                }

            }
        }

        return con;
    }

    private void createDatabase() throws SQLException {
        String url=Config.dbUrl.substring(0,Config.dbUrl.lastIndexOf("/")+1);
        if(Config.dbUrl.contains("?")){
            url+=Config.dbUrl.substring(Config.dbUrl.indexOf("?"));
        }
        String dbName= "";
        Matcher m=Pattern.compile("\\w+").matcher(Config.dbUrl.substring(Config.dbUrl.lastIndexOf("/")+1));
        if(m.find()){
            dbName=m.group(0);
        }
        Connection c=DriverManager.getConnection(url,Config.dbUser,Config.dbPassword);
        Statement s= c.createStatement();
        s.execute("create database "+dbName);
        s.close();
        c.close();
    }
}
