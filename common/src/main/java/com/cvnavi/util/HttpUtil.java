package com.cvnavi.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import com.cvnavi.proxy.ProxyProvider;
import com.cvnavi.web.WebContextCleanup;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HttpUtil {

	static Logger log = LogManager.getLogger(HttpUtil.class);

	public static HttpHost RANDOM_PROXY = new HttpHost("");

	static CloseableHttpClient httpclient = null;
	static {

		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			log.error(e);
		}
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
				new NoopHostnameVerifier());
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", socketFactory).register("http", new PlainConnectionSocketFactory()).build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(20);

		httpclient = HttpClients.custom().setConnectionManager(connectionManager).build();
		WebContextCleanup.registeCloseable(httpclient);

	}

	/**
	 * http get请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpGet(String urlString, HashMap<String, String> header, HashMap<String, String> cookie,
			HttpHost proxy, Level level) {
		HttpGet httpGet = new HttpGet(urlString);

		String result = "";
		if (proxy == RANDOM_PROXY) {
			result = doHttp(httpGet, header, cookie, ProxyProvider.getRandomProxy(), 2000, level);
			if (result.length() == 0) {
				result = doHttp(httpGet, header, cookie, null, 2000, level);
			}
		} else {
			result = doHttp(httpGet, header, cookie, proxy, 2000, level);
		}
		return result;
	}

	/**
	 * http get请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpGet(String urlString, HashMap<String, String> header, HashMap<String, String> cookie,HttpHost proxy) {
		return doHttpGet(urlString, header, cookie, proxy, Level.INFO);
	}
	
	/**
	 * http get请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpGet(String urlString, HashMap<String, String> header, HashMap<String, String> cookie) {
		return doHttpGet(urlString, header, cookie, null, Level.INFO);
	}

	/**
	 * http get请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpGet(String urlString) {
		return doHttpGet(urlString, null, null, null, Level.INFO);
	}

	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString, HashMap<String, String> params, HashMap<String, String> header,
			HashMap<String, String> cookie, HttpHost proxy, int timeout, Level level) {

		HttpPost httpPost = new HttpPost(urlString);
		if (params != null && params.size() > 0) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				log.error(e1);
			}
		}
		String result = "";
		if (proxy == RANDOM_PROXY) {
			result = doHttp(httpPost, header, cookie, ProxyProvider.getRandomProxy(), timeout, level);
			if (result.length() == 0) {
				result = doHttp(httpPost, header, cookie, null, timeout, level);
			}
		} else {
			result = doHttp(httpPost, header, cookie, proxy, timeout, level);
		}
		return result;
	}

	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString, byte[] b, HashMap<String, String> header,
			HashMap<String, String> cookie, HttpHost proxy, int timeout, Level level) {

		HttpPost httpPost = new HttpPost(urlString);
		httpPost.setEntity(new ByteArrayEntity(b));

		String result = "";
		if (proxy == RANDOM_PROXY) {
			result = doHttp(httpPost, header, cookie, ProxyProvider.getRandomProxy(), timeout, level);
			if (result.length() == 0) {
				result = doHttp(httpPost, header, cookie, null, timeout, level);
			}
		} else {
			result = doHttp(httpPost, header, cookie, proxy, timeout, level);
		}
		return result;
	}

	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString, HashMap<String, String> params, HashMap<String, String> header,
			HashMap<String, String> cookie, HttpHost proxy) {
 
		return doHttpPost(urlString, params, header, cookie, proxy, 5000, Level.INFO);
	}
	
	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString, HashMap<String, String> params, HashMap<String, String> header,
			HashMap<String, String> cookie) {
		return doHttpPost(urlString, params, header, cookie, null, 5000, Level.INFO);
	}

	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString, HashMap<String, String> params) {
		return doHttpPost(urlString, params, null, null, null, 5000, Level.INFO);
	}

	/**
	 * http post请求
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String doHttpPost(String urlString) {
		return doHttpPost(urlString, (HashMap<String, String>) null, null, null, null, 5000, Level.INFO);
	}

	public static String doHttp(HttpRequestBase requestMethod, HashMap<String, String> header,
			HashMap<String, String> cookie, HttpHost proxy, int timeout, Level level) {
		String result = "";
		CloseableHttpResponse resp = sendHttp(requestMethod, header, cookie, proxy, timeout, level);
		if (resp != null) {
			HttpEntity entity = resp.getEntity();
			try {
				result = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
			} catch (ParseException | IOException e) {
				log.error(e);
			} finally {
				if (resp != null) {
					try {
						resp.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return result;
	}

	public static CloseableHttpResponse sendHttp(HttpRequestBase requestMethod, HashMap<String, String> header,
			HashMap<String, String> cookie, HttpHost proxy, int timeout, Level level) {

		CloseableHttpResponse response1 = null;

		if (header != null && header.size() > 0) {
			for (Entry<String, String> entry : header.entrySet()) {
				requestMethod.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpClientContext context = HttpClientContext.create();
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectTimeout(timeout);
		configBuilder.setSocketTimeout(timeout);
		if(proxy!=null){
			configBuilder.setProxy(proxy);
		}
		context.setRequestConfig(configBuilder.build());

		if (cookie != null && cookie.size() > 0) {
			CookieStore cookieStore = new BasicCookieStore();
			String domain = requestMethod.getURI().getAuthority();

			if (domain.substring(domain.indexOf('.') + 1).contains(".")) {// 如果域名中只有一个点，就不要取子域名了。
				domain = domain.substring(domain.indexOf('.'));
			}

			for (Entry<String, String> entry : cookie.entrySet()) {
				BasicClientCookie c = new BasicClientCookie(entry.getKey(), entry.getValue());
				c.setPath("/");
				c.setDomain(domain);
				c.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
				cookieStore.addCookie(c);
			}
			context.setCookieStore(cookieStore);
		}
		log(requestMethod, context, level);
		try {
			response1 = httpclient.execute(requestMethod, context);
		} catch (Exception e) {
			log(requestMethod, context, Level.ERROR);
			if (e.getMessage() == null) {
				log.error(e.getCause());
			} else {
				log.error(e.getMessage());
			}
		}
		return response1;
	}

	public static void log(HttpRequestBase requestMethod, HttpClientContext context, Level level) {
		DefaultRoutePlanner planner = new DefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE);
		HttpHost host;
		try {
			host = URIUtils.extractHost(requestMethod.getURI());
			HttpRoute route = planner.determineRoute(host, requestMethod, context);
			String path = requestMethod.getURI().toString();
			path = path.substring(path.indexOf(host.toString()) + host.toString().length());
			log.log(level, route.toString() + path);
		} catch (HttpException e) {
			log.error(e);
		}
	}
}
