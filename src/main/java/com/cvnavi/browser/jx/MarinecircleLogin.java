package com.cvnavi.browser.jx;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.DataReceivedParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

public class MarinecircleLogin extends ListenerAdapter {

	static Logger log = LogManager.getLogger(MarinecircleLogin.class);
	
	private String DSId = null;
	private String uuid = null;
	private String operid = null;
	private HashMap<String, String> cookie = new HashMap<>();

	@Override
	public void onDataReceived(DataReceivedParams params) {
		if (params.getURL().endsWith("amf")) {
			String result = new String(params.getData());

			if (DSId == null && result.contains("DSMessagingVersion")) {
				Matcher m = Pattern.compile("(.){8}-(.){4}-(.){4}-(.){4}-(.){12}").matcher(result);
				if (m.find()) {
					DSId = m.group(0);
				}
			}

			if (result.contains("uuid")) {// uuid,operid
				String temp = result.substring(result.indexOf("uuid"));
				Matcher m = Pattern.compile("(.){8}-(.){4}-(.){4}-(.){4}-(.){12}").matcher(temp);
				if (m.find()) {
					uuid = m.group(0);
				}
				temp = result.substring(result.indexOf("operid"));
				m = Pattern.compile("\\d+").matcher(temp);
				if (m.find()) {
					operid = m.group(0); 
					createResult();
					synchronized (lock) {
						lock.notifyAll();
					}
				}
			}
		}
	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					CookieStorage cs = event.getBrowser().getCookieStorage();
					List<Cookie> list = cs.getAllCookies();
					for (Cookie c : list) {
						String key = c.getName().toLowerCase();
						if (key.contains("sessionid") || key.contains("_utm")) {
							cookie.put(c.getName(), c.getValue());
						}
					}
				} catch (Exception ex) {
					log.error(ex);
				}
			}
		}, 100);
	}

	private void createResult() {
		result += "DSId=" + DSId + "\n";
		result += "uuid=" + uuid + "\n";
		result += "operid=" + operid + "\n";
		for (Entry<String, String> ent : cookie.entrySet()) {
			result += ent.getKey() + "=" + ent.getValue() + "\n";
		}
		result=result.trim();
	}
}
