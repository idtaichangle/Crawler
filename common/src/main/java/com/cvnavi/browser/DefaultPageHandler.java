package com.cvnavi.browser;

import java.util.Timer;
import java.util.TimerTask;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.RequestCompletedParams;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

/**
 * 监听浏览器页面加载完成事件。
 * @author lixy
 *
 */
public class DefaultPageHandler extends ListenerAdapter {

	static Logger log = LogManager.getLogger(DefaultPageHandler.class);

	protected Browser browser;

	protected String result="";

	protected long waitUntil=0;

	protected Timer timer;
	protected boolean loadComplete=false;

	public DefaultPageHandler(int timeout) {
		super(timeout);
		waitUntil=System.currentTimeMillis()+timeout;
	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		if(System.currentTimeMillis()<waitUntil){
			if(timer!=null){
				timer.cancel();
			}
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					browser=event.getBrowser()==null?browser:event.getBrowser();
					loadComplete(browser);
				}
			}, getResponseDelay());
		}
	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
		if(System.currentTimeMillis()<waitUntil){
			if(timer!=null){
				timer.cancel();
			}
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					browser=event.getBrowser()==null?browser:event.getBrowser();
					loadComplete(browser);
				}
			}, getResponseDelay());
		}
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		if(System.currentTimeMillis()<waitUntil){
			if(timer!=null){
				timer.cancel();
			}
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					browser=event.getBrowser()==null?browser:event.getBrowser();
					loadComplete(browser);
				}
			}, getResponseDelay());
		}
	}


	@Override
	public void onCompleted(RequestCompletedParams params) {
		if(System.currentTimeMillis()<waitUntil){
			if(timer!=null){
				timer.cancel();
			}
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					browser=params.getBrowser()==null?browser:params.getBrowser();
					loadComplete(browser);
				}
			}, getResponseDelay());
		}
	}

	public void loadComplete(Browser browser){
		if(!loadComplete){
			loadComplete=true;
			if(browser!=null){
				result = browser.getHTML();
			}
			try {
				synchronized (lock) {
					lock.notifyAll();
				}
			} catch (Exception ex) {
				log.error(ex);
			}
		}
	}

	protected int getResponseDelay(){
		return 3000;
	}

	@Override
	public String getResult() {
		return result;
	}
}
