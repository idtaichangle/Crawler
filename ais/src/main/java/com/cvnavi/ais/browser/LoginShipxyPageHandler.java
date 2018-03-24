package com.cvnavi.ais.browser;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cvnavi.browser.ListenerAdapter;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

public class LoginShipxyPageHandler extends ListenerAdapter {

	public static final String KEY_SESSION_ID = "ASP.NET_SessionId";
	public static final String KEY_USER_AUTH = ".UserAuth2";
	String sessionId = null;
	String userAuth2 = null;
	String result="";

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (event.getValidatedURL().equals("http://www.shipxy.com/Home/Login")) {
					for (Cookie c : event.getBrowser().getCookieStorage().getAllCookies()) {
						if (c.getName().equals(KEY_SESSION_ID)) {
							sessionId = c.getValue();
						} else if (c.getName().equals(KEY_USER_AUTH)) {
							userAuth2 = c.getValue();
						}
					}
					if(event.getBrowser().getHTML().contains("登录成功")){
						event.getBrowser().loadURL("http://www.shipxy.com");
					}
				}
			}

		}, 100);

	}

	@Override
	public void onBeforeURLRequest(BeforeURLRequestParams params) {
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
