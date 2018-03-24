package com.cvnavi.browser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cvnavi.proxy.ProxyProvider;
import com.cvnavi.web.WebContextCleanup;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.Schedule;
import com.cvnavi.util.CmdExecutor;
import com.cvnavi.util.JavaExecutor;
import org.codehaus.jackson.map.ObjectMapper;

public class BrowserServiceInvoker  extends AbstractDailyTask implements AutoCloseable {
	
	static Logger log = LogManager.getLogger(BrowserServiceInvoker.class);

	static {
		WebContextCleanup.registeCloseable(new BrowserServiceInvoker());
	}

	static boolean browserServiceRunning = false;
	
	@Override
	public Schedule[] initSchedules() {
		return  new Schedule[] { new Schedule("00:00:30", "00:00:31", 30000) };
	}

	@Override
	public void doTask() {
	}
	
	@Override
	protected void scheduleBeginEvent(Schedule s) {
		//重启浏览器
		stopBrowserService();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		killBrowserService();
		startBrowserService();
	}

	static void testOrStartServer() {
		browserServiceRunning=false;
		String pid=findBrowserServicePID();
		if(pid==null){
			startBrowserService();
		}
		Socket socket = null;
		for (int i = 0; i < 200; i++) {
			try{
				Thread.sleep(100);
				socket = new Socket("127.0.0.1", BrowserService.port);
				browserServiceRunning = true;
				socket.close();
				break;	
			}catch(Exception ex){}
		}
		if(!browserServiceRunning){
			killBrowserService();
		}
	}

	static synchronized String sendCmd(String cmd) {
		if (!browserServiceRunning) {
			testOrStartServer();
		}

		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", BrowserService.port);
			socket.setSoTimeout(60 * 1000);
			socket.getOutputStream().write(cmd.getBytes());
			InputStream is = socket.getInputStream();
			byte[] b = new byte[0];
			byte[] buf = new byte[10240];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				byte[] temp = new byte[b.length + len];
				System.arraycopy(b, 0, temp, 0, b.length);
				System.arraycopy(buf, 0, temp, b.length, len);
				b = temp;
			}
			String result = new String(b).trim();
			// System.out.println(result);

			return result;
		} catch (Exception e) {
			log.error(e);
			browserServiceRunning = false;
		} finally {
			try {
				if (socket != null) {
					socket.getInputStream().close();
					socket.getOutputStream().close();
					socket.close();
				}
			} catch (Exception e) {
			}
		}
		return "";
	}

//	public static String loginMarinecircle() {
//		try {
//			String cmd = "cmd=" + BrowserService.CMD_LOGIN_MARINE_CIRCLE;
//			HttpHost proxy = ProxyProvider.getRandomProxy();
//			if (proxy != null) {
//				cmd += "&proxy=" + URLEncoder.encode(proxy.getHostName() + ":" + proxy.getPort(), "UTF-8");
//			}
//			return sendCmd(cmd);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	public static String loginShipxy(String userName, String password) {
//		try {
//			String cmd = "cmd=" + BrowserService.CMD_LOGIN_SHIPXY + "&userName=" + userName + "&password=" + password;
//			HttpHost proxy = ProxyProvider.getRandomProxy();
//			if (proxy != null) {
//				cmd += "&proxy=" + URLEncoder.encode(proxy.getHostName() + ":" + proxy.getPort(), "UTF-8");
//			}
//			return sendCmd(cmd);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}

	public static String visitePage(String url, String method,HttpHost proxy) {
		return visitePage(url,method,proxy,null,20000,null);
	}

	public static String visitePage(String url, String method,HttpHost proxy,HashMap<String,Object> params) {
		return visitePage(url,method,proxy,params,20000,null);
	}

	public static String visitePage(String url, String method,HttpHost proxy,HashMap<String,Object> params,int timeout,Class<?> listener) {
		HashMap<String,Object> map=new HashMap<>();
		map.put("action",BrowserService.ACTION_VISITE_PAGE);
		map.put("url",url);
		map.put("method",method);
		map.put("proxy",proxy!=null?proxy.toHostString():null);
		map.put("params",params);
		map.put("timeout",timeout);
		map.put("listener",listener!=null?listener.getName():null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json=mapper.writeValueAsString(map);
			return sendCmd(json);
		} catch (IOException e) {
			log.error(e);
		}
		return "";
	}

	@Override
	public void close() throws Exception {
		stopBrowserService();
	}

	/**
	 * 开启浏览器服务
	 */
	public static synchronized void startBrowserService(){
		log.info("start browser service");
		String property = null;
		String p = System.getenv("catalina.home");
		if (p == null) {
			p = System.getProperty("catalina.home");
		}
		if (p != null) {
			property = "catalina.home=" + p+" -Xms256m -Xmx512m ";
		}
		JavaExecutor.runMainClass(BrowserStartup.class.getName(), property);
	}
	
	/**
	 * 停止浏览器服务 
	 */
	public static synchronized void stopBrowserService() {
		log.info("stop browser service");

		Socket socket = null;

		HashMap<String,Object> map=new HashMap<>();
		map.put("action",BrowserService.ACTION_EXIT);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json=mapper.writeValueAsString(map);
			socket = new Socket("127.0.0.1", BrowserService.port);
			socket.getOutputStream().write(json.getBytes());
		} catch (IOException e) {
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 通过杀进程的方式关闭浏览器。
	 */
	public static synchronized void killBrowserService() {
		log.info("kill browser service");
		String pid = findBrowserServicePID();
		if (pid != null) {
			String os = System.getProperty("os.name");
			String[] cmd=null;
			if (os.toLowerCase().contains("windows")) {
				cmd = CmdExecutor.prepareCmd("taskkill.exe /pid " + pid);
			} else {
				cmd = CmdExecutor.prepareCmd("kill -9  " + pid);
			}
			String output = CmdExecutor.execCmd(cmd);
			log.debug(output);
		}
	}
	
	/**
	 * 查询浏览器的进程号。
	 */
	public static synchronized String findBrowserServicePID() {
		String[] cmd = CmdExecutor.prepareCmd("jps -l");
		String output = CmdExecutor.execCmd(cmd);
		// System.out.println(output);
		Matcher m = Pattern.compile("[0-9]+(?= " + BrowserStartup.class.getName() + ")").matcher(output);
		if (m.find()) {
			return m.group(0);
		}
		return null;
	}


	public static void main(String args[]) {
		String s = "";
		// s=getPage("http://ip.zdaye.com/?port=8080", null);
		// s = sendCmd("cmd=" + BrowserService.CMD_LOGIN_MARINE_CIRCLE);
//		s = loginMarinecircle();
		System.out.println(s);
	}
}
