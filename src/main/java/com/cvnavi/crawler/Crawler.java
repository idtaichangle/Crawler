package com.cvnavi.crawler;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.Level;

import com.cvnavi.browser.BrowserServiceInvoker;
import com.cvnavi.db.dao.ProxyDaoService;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;

public abstract class Crawler {

	public FetchHttpType getFetchType() {
		return FetchHttpType.HTTP_CLIENT;
	}

	public void start() {
		try{
			for (String url : getUrls()) {
				String content = visite(url);
				onResponse(url, content);
			}
		}catch(Exception ex){}
	}

	public abstract String[] getUrls();

	public abstract void onResponse(String url,String content);

	public String visite(String url) {
		String content = "";
		switch (getFetchType()) {
		case HTTP_CLIENT:
			content = HttpUtil.doHttpGet(url,null,null,getProxy(),Level.INFO);
			break;
		case JXBROWSER:
			content = BrowserServiceInvoker.getPage(url, getProxy());
			break;
		}
		return content;
	}

	public HttpHost getProxy() {
		return ProxyDaoService.getRandomProxy();
	}
	
	public Header getHeader(){
		return null;
	}
}
