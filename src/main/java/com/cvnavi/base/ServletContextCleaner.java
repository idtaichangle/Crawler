package com.cvnavi.base;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * web context销毁时，关闭资源。
 * 
 * @author lixy
 *
 */
@WebListener
public class ServletContextCleaner implements ServletContextListener {

	static Logger log = LogManager.getLogger(ServletContextCleaner.class);

	private static List<AutoCloseable> list = new ArrayList<>();

	/**
	 * servlet context是否有效(已经初始化，未被销毁)。
	 */
	public static boolean contextValid = false;

	/**
	 * 注册。当web app context销毁时，会关闭这些可关闭对象。
	 * 
	 * @param closeable
	 */
	public static void registeCloseable(AutoCloseable closeable) {
		if (list.indexOf(closeable) == -1) {
			list.add(closeable);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		contextValid = false;
		for (AutoCloseable closeable : list) {
			try {
				closeable.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		contextValid = true;
		log.info("============"+event.getServletContext().getContextPath()+" started."+"============");
	}

}
