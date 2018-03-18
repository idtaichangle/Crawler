package com.cvnavi.browser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BrowserService {

	static Logger log = LogManager.getLogger(BrowserService.class);

	public static final String CMD_EXIT = "exit";
	public static final String CMD_GET_SCODE = "get_scode";
	public static final String CMD_LOGIN_MARINE_CIRCLE = "login_marine_circle";
	public static final String CMD_GET_PAGE = "get_page";
	public static final String CMD_LOGIN_SHIPXY = "login_shipxy";

	public static int port = 55536;
	static ServerSocket serverSocket;

	public static void startServer() throws IOException {
		serverSocket = new ServerSocket(port);
		log.info("BrowserService listening...");
		new Thread("browser-server-thread") {
			public void run() {
				try {
					while (true) {
						Socket socket = serverSocket.accept();
						processRequest(serverSocket, socket);
					}
				} catch (Exception ex) {
					log.error(ex);
				}
			}
		}.start();
	}

	public static void stopServer() {
		try {
			Socket s = new Socket("127.0.0.1", port);
			s.getOutputStream().write(("cmd=" + CMD_EXIT).getBytes());
			s.getOutputStream().flush();
			s.getInputStream().close();
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
		}
	}

	public static void processRequest(ServerSocket serverSocket, Socket socket) {
		try {
			byte[] buf = new byte[1024];
			socket.getInputStream().read(buf);
			String input = new String(buf).trim();
			if (input.length() == 0) {
				return;
			}
			log.info(input);

			String[] ss = input.split("&");
			HashMap<String, String> params = new HashMap<>();
			for (String s : ss) {
				if (s.contains("=")) {
					params.put(s.split("=")[0], s.split("=")[1]);
				}
			}
			String cmd = params.get("cmd");
			String proxy = params.get("proxy");
			if (proxy != null) {
				proxy = URLDecoder.decode(proxy, "UTF-8");
			}
			String output = " ";
			if (CMD_EXIT.equals(cmd)) {
				try {
					socket.getOutputStream().close();
					socket.close();
					serverSocket.close();
				} catch (Exception ex) {
					log.error(ex);
				}
				try{
					BrowserStartup.getBrowser().dispose();
				}catch(Exception ex){}
				System.exit(0);
				return;
			} else if (CMD_GET_SCODE.equals(cmd)) {
				output = BrowserStartup.getBrowser().getScode(proxy);
			} else if (CMD_LOGIN_MARINE_CIRCLE.equals(cmd)) {
				output = BrowserStartup.getBrowser().loginMarinecircle(proxy);
			} else if (CMD_LOGIN_SHIPXY.equals(cmd)) {
				String userName = params.get("userName");
				String password = params.get("password");
				output = BrowserStartup.getBrowser().loginShipxy(userName, password, proxy);
			} else if (CMD_GET_PAGE.equals(cmd)) {

				String url = URLDecoder.decode(params.get("url"), "UTF-8");
				output = BrowserStartup.getBrowser().loadURL(url, proxy);
			}
			Document doc = Jsoup.parse(output);
			String body = doc.body().text();
			String logStr = body.length() > 70 ? body.substring(0, 70) + "..." : body;
			logStr = logStr.replace("\n", "");
			log.info(logStr);
			if (!socket.isClosed()) {
				socket.getOutputStream().write(output.getBytes());
			}
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			try {
				socket.getInputStream().close();
				socket.getOutputStream().close();
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
