package com.cvnavi.proxy;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.annotation.WebListener;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.db.dao.ProxyDaoService;
import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.Schedule;

/**
 * 先前验证过的代理，过一段时间后也可能失效。本类定时验证代理是否还有效。无效的代理要从alive_proxy表删除。
 * 
 * @author lixy
 *
 */
@WebListener
public class ProxyFilterTask extends AbstractDailyTask {

	private static Logger log = LogManager.getLogger(ProxyFilterTask.class);

	@Override
	public Schedule[] initSchedules() {
		return emptySchedules;
	}

	@Override
	public void doTask() {
		log.info("Start test alive proxy.");
		try {
			Collection<HttpHost> all = ProxyDaoService.loadAliveProxy();
			Collection<HttpHost> tested = ProxyTester.testProxy(all);
			ArrayList<HttpHost> toBeRemove = new ArrayList<>(all);
			toBeRemove.removeAll(tested);
			ProxyDaoService.deleteAliveProxy(toBeRemove);
			all.removeAll(toBeRemove);	
			log.info("Test complete. Remove " + toBeRemove.size() + " proxy." + all.size() + " proxy remain.");
		} catch (Exception ex) {
			log.error(ex);
		}
	}
}
