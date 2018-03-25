package com.cvnavi.ais.browser;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cvnavi.browser.DefaultPageHandler;
import com.cvnavi.browser.ListenerAdapter;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginShipxyPageHandler extends DefaultPageHandler {
	static Logger log = LogManager.getLogger(LoginShipxyPageHandler.class);

	public static final String KEY_SESSION_ID = "ASP.NET_SessionId";
	public static final String KEY_USER_AUTH = ".UserAuth2";
	String sessionId = null;
	String userAuth2 = null;

	public LoginShipxyPageHandler(int timeout) {
		super(timeout);
	}

	@Override
	protected int getResponseDelay() {
		return 1000;
	}

	@Override
	public void loadComplete(Browser browser) {
		if(!loadComplete) {
			loadComplete = true;

			if (browser.getURL().equals("http://www.shipxy.com/Home/Login")) {
				for (Cookie c : browser.getCookieStorage().getAllCookies()) {
					if (c.getName().equals(KEY_SESSION_ID)) {
						sessionId = c.getValue();
					} else if (c.getName().equals(KEY_USER_AUTH)) {
						userAuth2 = c.getValue();
					}
				}
				if(browser.getHTML().contains("登录成功")){
					browser.loadURL("http://www.shipxy.com");
				}
			}
		}
	}

	@Override
	public void onBeforeURLRequest(BeforeURLRequestParams params) {
		log.debug("requesting "+params.getURL());
		if (params.getURL().contains("SetShipKey")) {
			Matcher m = Pattern.compile("(?<=SS=)[0-9]+").matcher(params.getURL());
			if (m.find()) {
				String scode = m.group(0);
				result += KEY_SESSION_ID + "=" + sessionId + "\n";
				result += KEY_USER_AUTH + "=" + userAuth2 + "\n";
				result += "Scode=" + scode + "\n";
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		}
	}

	@Override
	public String getResult() {
		return result;
	}
}
