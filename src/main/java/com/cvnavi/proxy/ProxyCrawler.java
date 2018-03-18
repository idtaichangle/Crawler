package com.cvnavi.proxy;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.crawler.Crawler;
import com.cvnavi.util.DateUtil;
import com.cvnavi.util.Header;
import com.cvnavi.util.ResourceReader;

public class ProxyCrawler extends Crawler {

	protected  Logger log;
	static String[] urls = ResourceReader.readLines("/proxy_sites.txt").toArray(new String[0]);
	
	protected String IP_PORT_PATTERN = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9]):\\d{2,5}";
	protected String IP_PATTERN = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])";
	protected String PORT_PATTERN = "\\d{2,5}";
	protected String TIME_PATTERN = "(\\d{2,4}[-/])?\\d{2}[-/]\\d{2} \\d{1,2}:\\d{2}(:\\d{2})?";
	protected String TIME_PATTERN2 = "\\d+(?=分钟前)";
	protected String TIME_PATTERN3 = "\\d+(?=小时前)";

	protected Pattern p0 = Pattern.compile(IP_PORT_PATTERN);
	protected Pattern p1 = Pattern.compile(IP_PATTERN);
	protected Pattern p2 = Pattern.compile(PORT_PATTERN);
	protected Pattern p3 = Pattern.compile(TIME_PATTERN);
	protected Pattern p4 = Pattern.compile(TIME_PATTERN2);
	protected Pattern p5 = Pattern.compile(TIME_PATTERN3);

	Calendar calendar = Calendar.getInstance();

	protected Date lastCrawl = new Date(0);
	
	public ProxyCrawler() {
		log=LogManager.getLogger(this.getClass());
	}

	@Override
	public Header getHeader() {
		Header header = Header.createRandom();
		header.put("Upgrade-Insecure-Requests", "1");
		return header;
	}
	
	@Override
	public String[] getUrls() {
		return urls;
	}

	@Override
	public void onResponse(String url, String content) {
		crawlProxy(content);
	}

	public HashSet<HttpHost> crawlProxy(String content) {
		HashSet<HttpHost> set = new HashSet<>();
		
		String ip;
		HttpHost proxy;
		
		Matcher m1 = p1.matcher(content);

		while (m1.find()) {
			proxy=null;
			ip = m1.group(0);
			String sub = content.substring(m1.end());
			Matcher m2 = p2.matcher(sub);
			if (m2.find()) {
				proxy=new HttpHost(ip,Integer.parseInt(m2.group(0)));
			}
			if(proxy!=null){
				Date date = findDate(sub);

				if (date != null && (date.getTime() - lastCrawl.getTime() > 0)) {
					set.add(proxy);
				} else {
					set.add(proxy);
				}
			}
		}
		
		return set;
	}

	/**
	 * 提取代理验证时间。
	 * @param sub
	 * @return
	 */
	protected Date findDate(String sub) {
		Date date = null;
		Matcher m3 = p3.matcher(sub);
		if (m3.find()) {
			date = DateUtil.parse(m3.group(0));
		}
 
		if (date == null) {
			Matcher m4 = p4.matcher(sub);
			if (m4.find()) {
				int before = Integer.parseInt(m4.group(0));
				calendar.setTime(new Date());
				calendar.roll(Calendar.MINUTE, -before);
				date = calendar.getTime();
			}
		}

		if (date == null) {
			Matcher m5 = p5.matcher(sub);
			if (m5.find()) {
				int before = Integer.parseInt(m5.group(0));
				calendar.setTime(new Date());
				calendar.roll(Calendar.HOUR, -before);
				date = calendar.getTime();
			}
		}
		return date;
	}
}
