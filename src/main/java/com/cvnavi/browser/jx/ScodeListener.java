package com.cvnavi.browser.jx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cvnavi.ais.shipxy.ShipxyHeartbeat;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;

public class ScodeListener extends ListenerAdapter {
 
	@Override
	public void onBeforeURLRequest(BeforeURLRequestParams params) {
		if (params.getURL().contains("SetShipKey")) {
			Matcher m = Pattern.compile("(?<=SS=)[0-9]+").matcher(params.getURL());
			if (m.find()) {
				result = ShipxyHeartbeat.SCODE+"="+m.group(0);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		}
	}
}
