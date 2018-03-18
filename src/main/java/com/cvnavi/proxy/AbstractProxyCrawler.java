package com.cvnavi.proxy;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.db.ProxyDao;
import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.util.DateUtil;

/**
 * http代理ip抓取器
 * 
 * @author lixy
 *
 */
public abstract class AbstractProxyCrawler extends AbstractDailyTask {

	protected Logger log;

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

	public AbstractProxyCrawler() {
		log = LogManager.getLogger(this.getClass());
	}

	public abstract String[] getCrawlUrl();

	public abstract String getUrlContent(String url);

	@Override
	public void doTask() {
		log.info("Start crawl.");
		HashSet<HttpHost> newSet = new HashSet<>();
		try {
			for (String url : getCrawlUrl()) {
				newSet.addAll(doCrawl(url));
			}
			log.info("Found proxy:" + newSet.size());
			HashSet<HttpHost> tested = ProxyTester.testProxy(newSet);
			if (tested.size() > 0) {
				ProxyDao.saveAliveProxy(tested);
				Collection<HttpHost> aliveProxies = ProxyDao.getAliveProxies();
				aliveProxies.addAll(tested);
				log.info("Crawle complete.New proxy count:" + tested.size() + ",total proxy count:"
						+ aliveProxies.size());
			} else {
				log.info("Crawle complete.No new proxy.");
			}
		} catch (Exception ex) {
			log.error(ex);
		}
		lastCrawl = new Date();
	}

	public HashSet<HttpHost> doCrawl(String url) {
		HashSet<HttpHost> set = new HashSet<>();

		String s = getUrlContent(url);
		String ip;
		HttpHost proxy;

		Matcher m1 = p1.matcher(s);

		while (m1.find()) {
			proxy = null;
			ip = m1.group(0);
			String sub = s.substring(m1.end());
			Matcher m2 = p2.matcher(sub);
			if (m2.find()) {
				proxy = new HttpHost(ip, Integer.parseInt(m2.group(0)));
			}
			if (proxy != null) {
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
	 * 
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
