package com.cvnavi.web;

import com.cvnavi.db.DbChecker;
import com.cvnavi.proxy.dao.ProxyDaoService;
import com.cvnavi.proxy.ProxyProvider;
import com.cvnavi.task.WebBackgroundTaskScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class WebApplication implements ServletContextListener{

    static Logger log = LogManager.getLogger(WebApplication.class);

    /**
     * servlet context是否有效(已经初始化，未被销毁)。
     */
    public static boolean contextValid = false;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DbChecker.checkDatabase();
        WebBackgroundTaskScheduler.getInstance().startScheduler();
        ProxyDaoService.getInstance().loadAliveProxy();
        ProxyProvider.register(ProxyDaoService.getInstance());
        contextValid = true;
        log.info("============"+servletContextEvent.getServletContext().getContextPath()+" started."+"============");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        contextValid = false;
        WebBackgroundTaskScheduler.getInstance().stopScheduler();
        DbChecker.closeDatabase();
        WebContextCleanup.doClose();
    }
}
