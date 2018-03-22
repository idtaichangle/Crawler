package com.cvnavi.db;

import com.cvnavi.config.Config;
import com.cvnavi.util.ResourceReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DbChecker implements ServletContextListener{
    static Logger log= LogManager.getLogger(DbChecker.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {


        if(DBConnection.getInstance() instanceof MysqlConnection){
            ScriptRunner runner = new ScriptRunner(DBConnection.getInstance().get(), false, false);
            byte[] b=ResourceReader.readFile("/create_table_mysql.sql");
            String s=new String(b);
            try {
                runner.runScript(new BufferedReader(new StringReader(s)));
            } catch (IOException ex) {
                log.error(ex);
            } catch (SQLException ex) {
                log.error(ex);
            }
        }else if(DBConnection.getInstance() instanceof DerbyConnection){
            try {
                if(!existTable("alive_proxy")){
                    Statement st=DBConnection.getInstance().get().createStatement();
                    String sql= new String(ResourceReader.readFile("/create_table_derby.sql"));
                    st.execute(sql);
                    st.close();
                }
            }catch (Exception ex){
                log.error(ex);
            }
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(DBConnection.getInstance() instanceof DerbyConnection){
            try {
                DriverManager.getConnection(Config.dbUrl+";shutdown=true");
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    private static boolean existTable(String name) throws Exception{
        ResultSet rs=DBConnection.getInstance().get().getMetaData().getTables(null,null,null,null);
        while(rs.next()){
            String s=rs.getString("TABLE_NAME");
            if(s .equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
