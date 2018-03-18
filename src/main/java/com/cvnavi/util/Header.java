package com.cvnavi.util;

import java.util.HashMap;
import java.util.Random;

import org.apache.http.HttpHeaders;

public class Header extends HashMap<String, String> {

	public static String[] USER_AGENT = { "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0",
			"Mozilla/5.0 (Windows; U; en) AppleWebKit/533.19.4 (KHTML, like Gecko) AdobeAIR/24.0",
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 QIHU 360SE",
			"Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)" };

	public static Header createDefault() {
		Header h = new Header();
		h.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:49.0) Gecko/20100101 Firefox/49.0");
		return h;
	}

	public static Header createRandom() {
		Header h = new Header();
		h.put(HttpHeaders.USER_AGENT, randomUserAgent());
		return h;
	}

	public Header referer(String referer) {
		put(HttpHeaders.REFERER, referer);
		return this;
	}

	public static String randomUserAgent() {
		return USER_AGENT[new Random().nextInt(USER_AGENT.length)];
	}
}
