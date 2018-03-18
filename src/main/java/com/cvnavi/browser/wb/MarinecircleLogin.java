package com.cvnavi.browser.wb;

import java.util.HashMap;
import java.util.Map.Entry;

import com.webrenderer.swing.IBrowserCanvas;
import com.webrenderer.swing.event.NetworkEvent;

public class MarinecircleLogin extends ListenerAdapter {

	private String DSId = null;
	private String uuid = null;
	private String operid = null;
	private HashMap<String, String> cookie = new HashMap<>();

	@Override
	public void onDocumentComplete(NetworkEvent event) {
		IBrowserCanvas browser = (IBrowserCanvas) event.getSource();
//		IDocument doc = browser.getDocument();
//		doc.getDocumentSource();//这个是源码。

		if (browser.getCookie() != null) {
			for (String s : browser.getCookie().split(";")) {
				if (s.length() > 0) {
					cookie.put(s.split("=")[0], s.split("=")[1]);
				}
			}
		}

		synchronized (browser) {
			browser.notifyAll();
		}
	}

	@Override
	public void onHTTPResponse(NetworkEvent arg0) {
		String s = arg0.getResponseHeaders();
		if (s.contains("JSESSIONID=")) {
			cookie.put("JSESSIONID", s.substring(s.indexOf("JSESSIONID=") + 11, s.indexOf("JSESSIONID=") + 43));
		}
	}

	private void createResult() {
		result += "DSId=" + DSId + "\n";
		result += "uuid=" + uuid + "\n";
		result += "operid=" + operid + "\n";
		for (Entry<String, String> ent : cookie.entrySet()) {
			result += ent.getKey() + "=" + ent.getValue() + "\n";
		}
		result = result.trim();
	}
}
