package com.cvnavi.proxy;

import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebListener;

import org.apache.http.HttpHost;

import com.cvnavi.browser.BrowserServiceInvoker;
import com.cvnavi.db.ProxyDao;
import com.cvnavi.task.Schedule;
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
		HttpHost proxy=ProxyDao.getRandomProxy();
		String content=BrowserServiceInvoker.getPage(url,proxy);
		return content;
	}

	@Override
	public HashSet<HttpHost> doCrawl(String url) {
		if(url.contains("ip.zdaye.com")){
			return doZdayeCrawl(url);
		}else if(url.contains("www.data5u.com")){
			return doData5uCrawl(url);
		}else if(url.contains("www.goubanjia.com")){
			return doGoubanjiaCrawl(url);
		}
		return super.doCrawl(url);
	}

	public HashSet<HttpHost> doData5uCrawl(String url) {
		HashSet<HttpHost> set = new HashSet<>();

		String s = getUrlContent(url);
		String ip;
		HttpHost proxy;

		Matcher m1 = p1.matcher(s);

		while (m1.find()) {
			proxy = null;
			ip = m1.group(0);
			String sub = s.substring(m1.end());
			sub = sub.substring(sub.indexOf("port"));
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

	public HashSet<HttpHost> doGoubanjiaCrawl(String url) {
		HashSet<HttpHost> set = new HashSet<>();

		String s = getUrlContent(url);

		String ip;

		p1 = Pattern.compile("(?<=class=\"ip\">)(.)+(?=</td>)");
		Matcher m = p1.matcher(s);

		while (m.find()) {
			ip = getDisplay(m.group(0));
			if (ip.contains(":")) {
				set.add(HttpHost.create(ip));
			}
		}
		return set;
	}

	public String getDisplay(String src) {
		StringBuilder sb = new StringBuilder(src);
		int index = -1;
		while ((index = sb.indexOf("<")) > -1) {
			int index2 = sb.indexOf(">", index);
			String sub = sb.substring(index, index2 + 1);
			if (sub.contains("none")) {
				index2 = sb.indexOf(">", index2 + 1);
			}
			sb.replace(index, index2 + 1, "");
		}
		return sb.toString();
	}
}
