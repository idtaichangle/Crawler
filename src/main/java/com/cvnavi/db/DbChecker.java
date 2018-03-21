package com.cvnavi.db;

import com.cvnavi.config.Config;
import com.cvnavi.util.ResourceReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DbChecker implements ServletContextListener{
    static Logger log= LogManager.getLogger(DbChecker.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(Config.dbDriver.contains("derby")){
            try {
                if(!existTable("alive_proxy")){
                    Statement st=DBConnection.get().createStatement();
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
        if(Config.dbDriver.contains("derby")){
            try {
                DriverManager.getConnection(Config.dbUrl+";shutdown=true");
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    private static boolean existTable(String name) throws Exception{
        ResultSet rs=DBConnection.get().getMetaData().getTables(null,null,null,null);
        while(rs.next()){
            String s=rs.getString("TABLE_NAME");
            if(s .equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
