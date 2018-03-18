package com.cvnavi.browser.wb;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webrenderer.swing.IBrowserCanvas;
import com.webrenderer.swing.dom.IDocument;
import com.webrenderer.swing.event.NetworkEvent;

/**
 * 监听浏览器页面加载完成事件。
 * @author lixy
 *
 */
public class PageListener extends ListenerAdapter {

	static Logger log = LogManager.getLogger(PageListener.class);

	@Override
	public void onDocumentComplete(NetworkEvent event) {
		if (!"loadHTML".equals(event.getURL())) {
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					try{
						IBrowserCanvas browser = (IBrowserCanvas) event.getSource();
						IDocument doc = browser.getDocument();
						if(doc.getAll().length()>0){
							result = doc.getAll().item(0).getOuterHTML();
							synchronized (browser) {
								browser.notifyAll();
							}
						}
					}catch(Exception ex){
						log.error(ex);
					}
				}
			}, 100);
		}
	}
}
