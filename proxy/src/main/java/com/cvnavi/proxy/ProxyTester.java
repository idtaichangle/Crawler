package com.cvnavi.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.proxy.ProxyTestTask.TestResult;

/**
 * 验证代理是否有效
 * 
 * @author lixy
 *
 */
public class ProxyTester {
	static Logger log = LogManager.getLogger(ProxyTester.class);

	static ThreadPoolExecutor pool;
	/**
	 * 验证代理是否有效。
	 * 
	 * @param tr
	 * @return
	 */
	public static void testProxy(TestResult tr) {
		new Thread(new ProxyTestTask(tr)).start();
	}

	/**
	 * 验证代理是否有效。
	 * 
	 * @param set
	 *            返回有效的代理
	 * @return
	 */
	public static HashSet<HttpHost> testProxy(Collection<HttpHost> set) {
		HashSet<HttpHost> result = new HashSet<HttpHost>();
		if (set.size() == 0) {
			return result;
		}
		List<TestResult> temp = new ArrayList<>();

		pool = new ThreadPoolExecutor(150, 200, 10L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		pool.allowCoreThreadTimeOut(true);

		for (HttpHost proxy : set) {
			TestResult tr = new TestResult(proxy);
			temp.add(tr);
			pool.execute(new ProxyTestTask(tr));
		}

		pool.shutdown();
		try {
			pool.awaitTermination(150, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e);
		}
		pool.shutdownNow();

		for (TestResult tr : temp) {
			if (tr.isValid()) {
				result.add(tr.getProxy());
			}
		}
		return result;
	}

	public static void interrupt(){
		pool.shutdownNow();
	}
}
