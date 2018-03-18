package com.cvnavi.browser;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * 浏览器的启动类
 * @author lixy
 *
 */
public class BrowserStartup {

	static {
		ConfigurationFactory.setConfigurationFactory(new CustomConfigurationFactory());
	}

	static Logger log = LogManager.getLogger(BrowserStartup.class);

	static AbstractBrowser browser;
	
	public static void main(String[] args) {
		try {
			// 测试端口是否可用
			ServerSocket serverSocket = new ServerSocket(BrowserService.port);
			serverSocket.close();

			log.info("=========Start BrowserInterface.=========");
			
			startBrowserUI();

			BrowserService.startServer();
			
		} catch (Exception ex) {
			log.error(ex);
			System.exit(0);
		}
	}

	static void startBrowserUI() throws RuntimeException {
		try {
			browser = (AbstractBrowser) Class.forName("com.cvnavi.browser.jx.JxBrowser").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			log.error(e1);
			throw new RuntimeException("Can't initialize browser."+e1.getMessage());
		}
		
		javax.swing.JFrame f = new javax.swing.JFrame();
		f.setLayout(new GridLayout(1, 2));
		f.add(browser.getBrowserView());
		f.setSize(800, 600);
		f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				BrowserService.stopServer();
			}
		});
	}
	
	public static AbstractBrowser getBrowser() {
		return browser;
	}
}
