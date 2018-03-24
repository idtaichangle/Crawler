package com.cvnavi.browser;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.chromium.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JxBrowser  {
	static Logger log = LogManager.getLogger(JxBrowser.class);
	static Browser browser;
	static BrowserContext browserContext;
	static ListenerAdapter adapter = new ListenerAdapter(20000){

		@Override
		public String getResult() {
			return null;
		}
	};

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

	public void dispose() {
		browser.dispose();
	}

	public String visitePage(HashMap<String,Object> map) {
		String url=map.get("url").toString();
		String method=map.get("method").toString();
		String proxy=map.get("proxy")==null?null:map.get("proxy").toString();
		int timeout=map.get("timeout")==null?20000:Integer.parseInt(map.get("timeout").toString());
		HashMap<String,String>params= (HashMap<String, String>) map.get("params");


		ListenerAdapter listener=new DefaultPageHandler(20000);
		String listenerClass=DefaultPageHandler.class.getName();
		if(map.get("listener")!=null){
			listenerClass=map.get("listener").toString();
		}
		try {
			Class<? extends ListenerAdapter> clazz=(Class<? extends ListenerAdapter>) Class.forName(listenerClass);
			listener=clazz.getConstructor(int.class).newInstance(timeout);
		} catch (ClassNotFoundException e) {
			log.error(e);
		} catch (InstantiationException e) {
			log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e);
		}
		return visitePage(url,method,params,proxy,timeout,listener);
	}

	public String visitePage( String url, String method,HashMap<String,String> params, String proxy,int timeOut, ListenerAdapter listener) {
		adapter.setListener(listener);
		LoadURLParams lup = null;
		if ("get".equalsIgnoreCase(method)) {
			lup = new LoadURLParams(url);
		} else {
			String paramString="";
			if(params!=null){
				for(Map.Entry<String,String> entry:params.entrySet()){
					paramString+=entry.getKey()+"="+entry.getValue()+"&";
				}
				if(paramString.endsWith("&")){
					paramString=paramString.substring(0,paramString.length()-1);
				}
			}
			lup = new LoadURLParams(url, paramString);
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

		browser.loadHTML("<html></html>");

		return listener.getResult();
	}

}
