package com.cvnavi.browser.wb;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.cvnavi.ais.shipxy.ShipxyHeartbeat;
import com.webrenderer.swing.IBrowserCanvas;
import com.webrenderer.swing.event.NetworkEvent;

public class ScodeListener extends ListenerAdapter {

	private Timer timer;
	int i = 0;

	@Override
	public void onDocumentComplete(NetworkEvent event) {
		if (!"loadHTML".equals(event.getURL())) {
			IBrowserCanvas browser = (IBrowserCanvas) event.getSource();
			if(timer==null){
				timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						i++;
						String s = browser.executeScriptWithReturn("eval(window.scode)");
						if ((s != null && Pattern.matches("[0-9]+", s)) || i > 1000) {
							result = ShipxyHeartbeat.SCODE+"="+s;
							timer.cancel();
							synchronized (browser) {
								browser.notifyAll();
							}
						}
					}
				}, 100, 20);
			}
		}
	}
}
