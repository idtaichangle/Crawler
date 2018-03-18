package com.cvnavi.browser.wb;

import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.browser.AbstractBrowser;
import com.webrenderer.swing.BrowserFactory;
import com.webrenderer.swing.IBrowserCanvas;
import com.webrenderer.swing.ProxySetting;

public class WbBrowser extends AbstractBrowser{
	static Logger log = LogManager.getLogger(WbBrowser.class);

	private IBrowserCanvas browser;
	private ListenerAdapter adapter = new ListenerAdapter();
	private DialogWatcher watcher=new DialogWatcher();

	public WbBrowser() {
		BrowserFactory.setLicenseData("30dtrial", "RC9ERITP6GGQVOLDT7G4R4S31BGT1NOB");
		browser = BrowserFactory.spawnMozilla();
		browser.enableImageLoading(false);
		browser.disableCache();
		browser.addNetworkListener(adapter);
	}

	public  JComponent getBrowserView() {
		return (JComponent) browser.getComponent();
	}
	
	@Override
	public void dispose() {
	}

	public String loadURL(String url, String proxy) {
		return visitePage(HTTP_GET, url, null, 10000, proxy, new PageListener());
	}

	public String getScode(String proxy) {
		String url = "http://www.shipxy.com";
		return visitePage(HTTP_GET, url, null, 40000, proxy, new ScodeListener());
	}

	public String loginMarinecircle(String proxy) {
		String url = "http://www.marinecircle.com/home.jsp?isDemoUser=true&from=home";
		return visitePage(HTTP_GET, url, null, 40000, proxy, new MarinecircleLogin());
	}

	public String loginShipxy(String userName, String password,String proxy) {
		String url = "http://www.shipxy.com/Home/Login";
		String params = "Model.UserName=" + userName + "&Model.Password=" + password;
		return visitePage(HTTP_POST, url, params, 50000, proxy, new LoginShipxyListener());
	}

	String visitePage(int getOrPost, String url, String params, int timeOut, String proxy, ListenerAdapter listener) {
		if (proxy != null) {
			ProxySetting ps = new ProxySetting(ProxySetting.PROTOCOL_ALL, proxy.split(":")[0],
					Integer.parseInt(proxy.split(":")[1]));
			browser.setProxyProtocol(ps);
			browser.enableProxy();
		} else {
			browser.disableProxy();
		}

		adapter.setListener(listener);
		watcher.startWatch();
		if (getOrPost == HTTP_GET) {
			browser.loadURL(url);
		} else {
			browser.httpPOST(url, params);
		}
		synchronized (browser) {
			try {
				browser.wait(timeOut);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
		adapter.setListener(null);
		browser.deleteCookies();
		browser.stopLoad();
		watcher.stopWatch();
		new Thread() {
			public void run() {
				browser.loadHTML("<html></html>", "");
				browser.stopLoad();
			};
		}.start();
		return listener.result;
	}
}
