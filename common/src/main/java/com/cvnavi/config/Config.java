package com.cvnavi.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author lixy
 *
 */
public class Config {
	static Logger log = LogManager.getLogger(Config.class);

	public static String dbDriver;
	public static String dbUrl;
	public static String dbUser;
	public static String dbPassword;

	public static String mailUser = "";
	public static String mailPassword = "";
	public static String mailSmtpHost = "";
	public static String mailFrom = "";
	public static String mailTo = "";

	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	static {
		Properties p = new Properties();
		try {
			InputStream is = Config.class.getResourceAsStream("/config.properties");
			p.load(is);

			dbDriver = p.getProperty("db.driver");
			dbUrl = p.getProperty("db.url");
			dbUser = p.getProperty("db.user");
			dbPassword = p.getProperty("db.password");

			mailUser = p.getProperty("mail.user");
			mailPassword = p.getProperty("mail.password");
			mailSmtpHost = p.getProperty("mail.smtp.host");
			mailFrom = p.getProperty("mail.from");
			mailTo = p.getProperty("mail.to");

			is.close();
		} catch (IOException e) {
			log.error(e);
		}
	}
	

}
