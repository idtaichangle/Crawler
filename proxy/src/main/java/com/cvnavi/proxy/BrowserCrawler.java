package com.cvnavi.proxy;

import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebListener;

import com.cvnavi.db.dao.ProxyDaoService;
import com.cvnavi.task.Schedule;
import org.apache.http.HttpHost;

import com.cvnavi.browser.BrowserServiceInvoker;
import com.cvnavi.util.ResourceReader;

/**
 * 通过jxBrowser浏览器抓取代理ip。
 * 
 * @author lixy
 *
 */

@WebListener
public class BrowserCrawler extends AbstractProxyCrawler {

	static String[] urls = ResourceReader.readLines("/proxy_sites2.txt").toArray(new String[0]);

	@Override
	public String[] getCrawlUrl() {
		return urls;
	}

	@Override
	public Schedule[] initSchedules() {
		return emptySchedules;
	}

	public String getUrlContent(String url) {
		HttpHost proxy= ProxyProvider.getRandomProxy();
		String content=BrowserServiceInvoker.visitePage(url,"get",proxy);
		return content;
	}

	@Override
	public HashSet<HttpHost> doCrawl(String url) {
		if(url.contains("ip.zdaye.com")){
			return doZdayeCrawl(url);
		}
		return super.doCrawl(url);
	}

	public HashSet<HttpHost> doZdayeCrawl(String url) {
		HashSet<HttpHost> set = new HashSet<>();

		String s = getUrlContent(url);
		String ip;
		int port = Integer.parseInt(url.split("=")[1]);

		Matcher m = p1.matcher(s);

		while (m.find()) {
			ip = m.group(0);
			set.add(new HttpHost(ip, port));
		}
		return set;
	}
}
