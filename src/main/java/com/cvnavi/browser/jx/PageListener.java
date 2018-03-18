package com.cvnavi.browser.jx;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

/**
 * 监听浏览器页面加载完成事件。
 * @author lixy
 *
 */
public class PageListener extends ListenerAdapter {

	static Logger log = LogManager.getLogger(PageListener.class);

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					result = event.getBrowser().getHTML();
					synchronized (lock) {
						lock.notifyAll();
					}
				} catch (Exception ex) {
					log.error(ex);
				}
			}
		}, 100);
	}
}
