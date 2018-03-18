package com.cvnavi.distance;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.util.HttpUtil;

public class DistanceCmd {


	public static void main(String args[]) {
		openSession();
		String s=getDistance("CN0144", "CN0407");
		System.out.println(s);
		 
	}

	static Logger log = LogManager.getLogger(DistanceCmd.class);

	protected static final String url = "http://www.marinecircle.com/messagebroker/amf";
	static final String p1="00030000000100046e756c6c00022f31000000e00a00000001110a81134d666c65782e6d6573736167696e672e6d657373616765732e436f6d6d616e644d657373616765136f7065726174696f6e1b636f7272656c6174696f6e4964136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e040506010649{messageId}04000a0b012544534d6573736167696e6756657273696f6e0401094453496406076e696c0104000a0501010601";
	static final String p2="00030000000100046e756c6c00022f31000001b70a00000001110a81134f666c65782e6d6573736167696e672e6d657373616765732e52656d6f74696e674d6573736167650d736f75726365136f7065726174696f6e136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e065b636f6d2e73746f6e65736f66742e636c69656e742e666c65782e5041466c6578436c69656e74416461707465720615646f427573696e6573730649{messageId}04000a0b0109445349640649{DSId}2144535265717565737454696d656f757404819c20154453456e64706f696e74010104000905010a535d636f6d2e73746f6e65736f66742e64746f2e627573696e6573732e496e506172616d5041416c6c446963745172791546756e6374696f6e49441546756e634d656e7549440d4f70657249440d4469637449441544696374547970654944010101010481f21b060101061f676f7363702d706173657276696365";
	static final String p3="00030000000100046e756c6c00022f32000001dc0a00000001110a81134f666c65782e6d6573736167696e672e6d657373616765732e52656d6f74696e674d6573736167650d736f75726365136f7065726174696f6e136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e065b636f6d2e73746f6e65736f66742e636c69656e742e666c65782e5041466c6578436c69656e74416461707465720615646f427573696e6573730649{messageId}04000a0b0109445349640649{DSId}2144535265717565737454696d656f757404819c20154453456e64706f696e74010104000905010a535d636f6d2e73746f6e65736f66742e64746f2e627573696e6573732e496e506172616d5041416c6c446963745172791546756e6374696f6e49441546756e634d656e7549440d4f70657249440d44696374494415446963745479706549440101010104819d050601064946304132333044362d334230422d313433422d463546412d343931314433453641434431061f676f7363702d706173657276696365";
	static final String p4="00030000000100046e756c6c00022f31000001010a00000001110a81134d666c65782e6d6573736167696e672e6d657373616765732e436f6d6d616e644d657373616765136f7065726174696f6e1b636f7272656c6174696f6e4964136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e040506010649{messageId}04000a0b012544534d6573736167696e6756657273696f6e040109445349640649{DSId}0104000a0501010601";
	static final String p5="00030000000100046e756c6c00022f320000014e0a00000001110a81134f666c65782e6d6573736167696e672e6d657373616765732e52656d6f74696e674d6573736167650d736f75726365136f7065726174696f6e136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e0663636f6d2e73746f6e65736f66742e636c69656e742e666c65782e476c6f62616c466c6578436c69656e7441646170746572061f6d6f62696c6544656d6f4c6f67696e0649{messageId}04000a0b0109445349640649{DSId}154453456e64706f696e7401010400090301061b4c696e757820332e342e38302b01061b676c6f62616c73657276696365";
	static final String pd = "00030000000100046e756c6c00022f320000022c0a00000001110a81134f666c65782e6d6573736167696e672e6d657373616765732e52656d6f74696e674d6573736167650d736f75726365136f7065726174696f6e136d65737361676549641574696d65546f4c6976650f686561646572731374696d657374616d7009626f647911636c69656e7449641764657374696e6174696f6e065b636f6d2e73746f6e65736f66742e636c69656e742e666c65782e4154466c6578436c69656e74416461707465720615646f427573696e6573730649{messageId}04000a0b012144535265717565737454696d656f757404819c20154453456e64706f696e740109445349640649{DSId}0104000905010a6365636f6d2e73746f6e65736f66742e64746f2e627573696e6573732e496e506172616d415443616c63446973344d6f62696c651546756e6374696f6e49441546756e634d656e7549440d4f70657249440f506f72745374721b457863526f757465506f696e74154e6176466f726d756c6101010615{operid}061b{from}2c{to}064d2d322c31393636352c35333832392c31333536372c32353039312c32323434352c3631373035010649{uuid}01061f676f7363702d617473657276696365";

	static String jsessionid=null;
	static String DSId = null;
	static String uuid = null;
	public static String operid = null;

	public static final String LIMIT = "Your day limit is over.";
	
	static HashMap<String, String> cookie = new HashMap<>();
	static HashMap<String, String> header = new HashMap<String, String>();

	static {
		header.put(HttpHeaders.CONTENT_TYPE, "application/x-amf");
		header.put(HttpHeaders.REFERER, "app:/McDistance.swf");
		header.put(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");
		header.put(HttpHeaders.ACCEPT, "*/*");
		header.put("x-flash-version", "24,0,0,174");
		header.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Android; U; en) AppleWebKit/533.19.4 (KHTML, like Gecko) AdobeAIR/20.0");
	}

	public static String getDistance(String from, String to) {
		if (DSId == null || uuid == null || operid == null) {
			return null;
		}
		String p = pd.replace("{DSId}", bytesToString(DSId.getBytes()));
		p = p.replace("{uuid}", bytesToString(uuid.getBytes()));
		p = p.replace("{operid}", bytesToString(operid.getBytes()));
		p = p.replace("{from}", bytesToString(from.getBytes()));
		p = p.replace("{to}", bytesToString(to.getBytes()));
		p = p.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));

		byte[] b = stringToBytes(p);

		String result = HttpUtil.doHttpPost(url, b, header, cookie, HttpUtil.RANDOM_PROXY, 20000, Level.INFO);
		if (result.contains(LIMIT)) {
			return LIMIT;
		}
		String distance = getResult(result);
		for (int i = 0; i < 10; i++) {
			if (distance == null) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				result = HttpUtil.doHttpPost(url, b, header, cookie, HttpUtil.RANDOM_PROXY, 20000, Level.INFO);
				distance = getResult(result);
			} else {
				break;
			}
		}

		return distance;
	}

	private static String getResult(String result) {
		if (result.contains("onResult") && result.contains("distance")) {
			String temp = result.substring(result.indexOf("distance") + 10);
			return temp.substring(0, temp.indexOf(","));
		}
		return null;
	}

	public static byte[] stringToBytes(String hex) {
		byte[] b = new byte[hex.length() / 2];
		String sub;
		for (int i = 0; i < hex.length(); i = i + 2) {
			sub = hex.substring(i, i + 2);
			b[i / 2] = (byte) Integer.parseInt(sub, 16);
		}
		return b;
	}

	public static String bytesToString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (byte ab : b) {
			String s = Integer.toHexString(ab & 0xff);
			if (s.length() == 1) {
				s = "0" + s;
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static synchronized boolean openSession() {
		String p = p1.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));
		byte[] b = stringToBytes(p);
		String result = HttpUtil.doHttpPost(url, b,header, cookie, null, 20000, Level.INFO);
		
		Matcher m = Pattern.compile("(?<=jsessionid=)(.){32}").matcher(result);
		if (m.find()) {
			jsessionid = m.group(0);
		}
		m = Pattern.compile("(.){8}-(.){4}-(.){4}-(.){4}-(.){12}").matcher(result);
		if (m.find()) {
			DSId = m.group(0);
		}

		if(jsessionid==null || DSId==null){
			return false;
		} 

		cookie.put("JSESSIONID", jsessionid);

		p = p2.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));
		p = p.replace("{DSId}", bytesToString(DSId.getBytes()));
		b =stringToBytes(p);
		result = HttpUtil.doHttpPost(url + ";jsessionid=" + jsessionid, b, header, cookie,null,20000,Level.DEBUG);

		p = p3.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));
		p = p.replace("{DSId}", bytesToString(DSId.getBytes()));
		b =stringToBytes(p);
		result = HttpUtil.doHttpPost(url + ";jsessionid=" + jsessionid, b, header, cookie,null,20000,Level.DEBUG);
 
		p = p4.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));
		p = p.replace("{DSId}", bytesToString(DSId.getBytes()));
		b =stringToBytes(p);
		result = HttpUtil.doHttpPost(url, b, header, cookie,null,20000,Level.DEBUG);
		
		p = p5.replace("{messageId}", bytesToString(UUID.randomUUID().toString().toUpperCase().getBytes()));
		p = p.replace("{DSId}", bytesToString(DSId.getBytes()));
		b =stringToBytes(p);
		result = HttpUtil.doHttpPost(url, b, header, cookie,null,20000,Level.DEBUG);

		if (result.contains("uuid")) {
			m = Pattern.compile("(.){8}-(.){4}-(.){4}-(.){4}-(.){12}")
					.matcher(result.substring(result.indexOf("uuid")));
			if (m.find()) {
				uuid = m.group(0);
			}

			String temp = result.substring(result.indexOf("operid") + 11);
			m = Pattern.compile("\\d+").matcher(temp);
			if (m.find()) {
				operid = m.group(0);
			}
		}
		log.info("jsessionid:"+jsessionid);
		log.info("DSId:"+DSId);
		log.info("uuid:"+uuid);
		log.info("operid:"+operid);
		return true;
	}
	
//	public static String doHttpPost(String urlString, byte[] b, HashMap<String, String> header,
//			HashMap<String, String> cookie, HttpHost proxy, int timeout, Level level) {
//		CloseableHttpClient httpClient=HttpClients.createDefault();
//		HttpPost httpPost = new HttpPost(urlString);
//		httpPost.setEntity(new ByteArrayEntity(b));
//		if (header != null && header.size() > 0) {
//			for (Entry<String, String> entry : header.entrySet()) {
//				httpPost.setHeader(entry.getKey(), entry.getValue());
//			}
//		}
//
//		HttpClientContext context = HttpClientContext.create();
//		RequestConfig.Builder configBuilder = RequestConfig.custom();
//		configBuilder.setConnectTimeout(timeout);
//		configBuilder.setSocketTimeout(timeout);
//		configBuilder.setProxy(proxy);
//		context.setRequestConfig(configBuilder.build());
//
//		if (cookie != null && cookie.size() > 0) {
//			CookieStore cookieStore = new BasicCookieStore();
//			String domain = httpPost.getURI().getAuthority();
//
//			if (domain.substring(domain.indexOf('.') + 1).contains(".")) {// 如果域名中只有一个点，就不要取子域名了。
//				domain = domain.substring(domain.indexOf('.'));
//			}
//
//			for (Entry<String, String> entry : cookie.entrySet()) {
//				BasicClientCookie c = new BasicClientCookie(entry.getKey(), entry.getValue());
//				c.setPath("/");
//				c.setDomain(domain);
//				c.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
//				cookieStore.addCookie(c);
//			}
//			context.setCookieStore(cookieStore);
//		}
//		try {
//			CloseableHttpResponse response1 = httpClient.execute(httpPost, context);
//			String s=EntityUtils.toString(response1.getEntity());
//			response1.close();
//			return s;
//		} catch (Exception e) {
//			if (e.getMessage() == null) {
//				log.error(e.getCause());
//			} else {
//				log.error(e.getMessage());
//			}
//		}
//		
//		return "";
//	}
}
