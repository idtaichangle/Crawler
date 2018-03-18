package com.cvnavi.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.base.ServletContextCleaner;
import com.cvnavi.config.Config;

public class DBConnection{
	static Logger log=LogManager.getLogger(DBConnection.class);
	private static Connection con;

	/**
	 * 获取数据库连接。使用完毕后，可以不用关闭连接。web app销毁时会关闭连接。
	 * @return
	 */
	public static Connection get() {
		if(!ServletContextCleaner.contextValid){
			return null;
		}
		try {
			if (con == null || con.isClosed()) {
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
