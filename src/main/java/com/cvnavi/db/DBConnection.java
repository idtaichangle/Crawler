package com.cvnavi.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.base.ServletContextCleaner;
import com.cvnavi.config.Config;

public abstract class DBConnection{

	private static DBConnection inst;

	public static DBConnection getInstance(){
		if(inst==null){
			if (Config.dbDriver.contains("mysql")){
				inst= new MysqlConnection();
			}else if (Config.dbDriver.contains("derby")){
				inst= new DerbyConnection();
			}
		}

		return inst;
	}

	public abstract Connection get();

}
