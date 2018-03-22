package com.cvnavi.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.base.KeyValue;
import com.cvnavi.db.dao.ProxyDaoService;

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

	/**
	 * 测试代理是否有效的Url。
	 */
	public static String proxyTestUrl = "http://freedll.shipxy.com/dll/dp.dll";
	/**
	 * 测试代理是否有效的关键字。用代理请求{@code proxyTestUrl},如果返回的内容，
	 * 以@{proxyTestKeyword}开始，则认为通过测试
	 */
	public static String proxyTestKeyword = "{status:";
	/**
	 * 验证代理url时尝试次数
	 */
	public static int proxyTestRetry = 2;
	/**
	 * 认为代理有效的次数。（例如，代理测试10次，至少有５次是通过测试，则认为此代理有效）
	 */
	public static int proxyTestThreshould = 2;

	public static String mailUser = "";
	public static String mailPassword = "";
	public static String mailSmtpHost = "";
	public static String mailFrom = "";
	public static String mailTo = "";
	

	public static String shipxyUser;
	public static String shipxyPassword;

	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	static {
		Properties p = new Properties();
		try {
			InputStream is = ProxyDaoService.class.getResourceAsStream("/config.properties");
			p.load(is);

			dbDriver = p.getProperty("db.driver");
			dbUrl = p.getProperty("db.url");
			dbUser = p.getProperty("db.user");
			dbPassword = p.getProperty("db.password");

			proxyTestUrl = p.getProperty("proxy.test.url");
			proxyTestKeyword = p.getProperty("proxy.test.keyword");
			proxyTestRetry = Integer.parseInt(p.getProperty("proxy.test.retry"));
			proxyTestThreshould = Integer.parseInt(p.getProperty("proxy.test.threshould"));

			mailUser = p.getProperty("mail.user");
			mailPassword = p.getProperty("mail.password");
			mailSmtpHost = p.getProperty("mail.smtp.host");
			mailFrom = p.getProperty("mail.from");
			mailTo = p.getProperty("mail.to");


			shipxyUser = p.getProperty("shipxy.user");
			shipxyPassword = p.getProperty("shipxy.password");
			is.close();
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	public static KeyValue<String> getRandomShipxyAccount(){
		String [] array=shipxyUser.split(",");
		int index=new Random().nextInt(array.length);
		String userName=array[index];
		String password=shipxyPassword.split(",")[index];
		return new KeyValue<String>(userName, password);
	}
}
