package com.cvnavi.proxy;

import java.io.IOException;

import com.cvnavi.web.WebContextCleanup;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.proxy.Config;

/**
 * 验证代理是否有效。测试{@code TEST_RETRY}次，
 * 如果连接成功次数与总测试次数的比例高于{@code TEST_FACTOR}，则认为此代理是有效的。
 * <p>
 * 调用最多的地址为freedll.shipxy.com。测试代理ip时，应测试本地址的访问速度。
 * freedll.shipxy.com和www.elane.com在同一个服务器上，ip为60.205.113.217。
 * 另外还发现，该Ｃ段上还有另外一台服务器:www.baiyinxianhuo.org，ip为60.205.113.229。
 * </p>
 * 
 * @author lixy
 *
 */
public class ProxyTestTask implements Runnable {

	static Logger log=LogManager.getLogger(ProxyTestTask.class);
	
	static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	static CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
	static{
		WebContextCleanup.registeCloseable(httpclient);
	}

	private HttpHost proxy;

	private TestResult result;

	public ProxyTestTask(TestResult result) {
		this.result = result;
		this.proxy = result.proxy;
		result.testComplete = false;
		result.valid = false;
	}


	@Override
	public void run() {
		log.debug("start test:"+proxy.toString());
		int success=0;
		for (int i = 0; i < Config.proxyTestRetry; i++) {
//			if(!WebApplication.contextValid){//如果servlet context关闭。
//				if (result != null) {
//					result.testComplete = true;
//					result.valid = false;
//				}
//				return;
//			}
			success += (doTest(proxy) ? 1 : 0);
		}
		boolean valid = (success>=Config.proxyTestThreshould);
		if (result != null) {
			result.testComplete = true;
			result.valid = valid;
		}
	}

	public static boolean doTest(HttpHost proxy) {
		String content = "";
		try {
			content = sendTestRequest(proxy);
			if (content.startsWith(Config.proxyTestKeyword)) {// {status: 98}
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	private static String sendTestRequest(HttpHost proxy) throws ClientProtocolException, IOException, ParseException {

		String result = "";
		HttpClientContext context = HttpClientContext.create();
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectTimeout(800);
		configBuilder.setSocketTimeout(800);
		configBuilder.setProxy(proxy);
		context.setRequestConfig(configBuilder.build());
		CloseableHttpResponse response1 = null;
		try {
			response1 = httpclient.execute(new HttpGet(Config.proxyTestUrl), context);
			HttpEntity entity1 = response1.getEntity();
			result = EntityUtils.toString(entity1);
			EntityUtils.consume(entity1);
		} finally {
			if (response1 != null) {
				try {
					response1.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	public static class TestResult {
		private HttpHost proxy;
		private boolean valid = false;
		private boolean testComplete = false;

		public TestResult(HttpHost proxy) {
			super();
			this.proxy = proxy;
		}

		public HttpHost getProxy() {
			return proxy;
		}

		public void setProxy(HttpHost proxy) {
			this.proxy = proxy;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public boolean isTestComplete() {
			return testComplete;
		}

		public void setTestComplete(boolean testComplete) {
			this.testComplete = testComplete;
		}
	}
}
