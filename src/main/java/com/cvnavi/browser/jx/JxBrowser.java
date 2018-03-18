package com.cvnavi.browser.jx;

import java.util.TimerTask;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.chromium.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.browser.AbstractBrowser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JxBrowser extends AbstractBrowser {
	static Logger log = LogManager.getLogger(JxBrowser.class);
	static Browser browser;
	static BrowserContext browserContext;
	static ListenerAdapter adapter = new ListenerAdapter();

	static {
		JxbrowserCracker.crack();
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			String switches = "--ppapi-flash-path=/usr/lib/adobe-flashplugin/libpepflashplayer.so";
			BrowserPreferences.setChromiumSwitches(switches);
		}

		BrowserContextParams params = new BrowserContextParams(BrowserPreferences.getDefaultDataDir());
		params.setStorageType(StorageType.MEMORY);
		browserContext = new BrowserContext(params);
		browserContext.getNetworkService().setNetworkDelegate(adapter);
	}

	public JxBrowser() {
		JxbrowserCracker.crack();
		browser = new Browser(browserContext);
		BrowserPreferences preferences = browser.getPreferences();
		preferences.setDefaultEncoding("UTF-8");
		preferences.setImagesEnabled(false);
		preferences.setAllowRunningInsecureContent(true);
		browser.setPreferences(preferences);
		browser.addLoadListener(adapter);
	}

	public JComponent getBrowserView() {
		return new BrowserView(browser);
	}
	
	@Override
	public void dispose() {
		browser.dispose();
	}

	public String loadURL(String url, String proxy) {
		return visitePage(HTTP_GET, url, null, proxy,10000, new PageListener());
	}

	public String getScode(String proxy) {
		String url = "http://www.shipxy.com";
		return visitePage(HTTP_GET, url, null, proxy,40000, new ScodeListener());
	}

	public String loginMarinecircle(String proxy) {
		String url = "http://www.marinecircle.com/home.jsp?isDemoUser=true&from=home#";
		return visitePage(HTTP_GET, url, null, proxy,40000, new MarinecircleLogin());
	}

	public String loginShipxy(String userName, String password,String proxy) {
		String url = "http://www.shipxy.com/Home/Login";
		String params = "Model.UserName=" + userName + "&Model.Password=" + password;
		return visitePage(HTTP_POST, url, params, proxy,40000, new LoginShipxyListener());
	}

	String visitePage(int getOrPost, String url, String params, String proxy,int timeOut, ListenerAdapter listener) {
		adapter.setListener(listener);
		LoadURLParams lup = null;
		if (getOrPost == HTTP_GET) {
			lup = new LoadURLParams(url);
		} else {
			lup = new LoadURLParams(url, params);
		}

		ProxySerivce  proxyService = browserContext.getProxyService();
		if(proxy==null || proxy.isEmpty()){
			proxyService.setProxyConfig(new DirectProxyConfig());
		}else{
			String proxyRules = "http=foo:80;https=foo:80;ftp=foo:80;socks=foo:80";
			proxyRules=proxyRules.replaceAll("foo:80",proxy);
			proxyService.setProxyConfig(new CustomProxyConfig(proxyRules));
		}
		browser.loadURL(lup);
		synchronized (listener.lock) {
			try {
				listener.lock.wait(timeOut);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}

		adapter.setListener(null);
		browser.stop();
		browser.getCacheStorage().clearCache();
		 
		new java.util.Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				browser.loadHTML("<html></html>");
			}
		}, 100);
		return listener.result;
	}

}
