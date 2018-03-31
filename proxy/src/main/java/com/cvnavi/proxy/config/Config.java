package com.cvnavi.proxy.config;

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


	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	static {
		Properties p = new Properties();
		try {
			InputStream is = Config.class.getResourceAsStream("/proxy.properties");
			p.load(is);

			proxyTestUrl = p.getProperty("proxy.test.url");
			proxyTestKeyword = p.getProperty("proxy.test.keyword");
			proxyTestRetry = Integer.parseInt(p.getProperty("proxy.test.retry"));
			proxyTestThreshould = Integer.parseInt(p.getProperty("proxy.test.threshould"));

			is.close();
		} catch (IOException e) {
			log.error(e);
		}
	}
	

}
