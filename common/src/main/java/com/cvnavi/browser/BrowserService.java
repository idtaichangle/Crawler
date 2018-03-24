package com.cvnavi.browser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BrowserService {

	static Logger log = LogManager.getLogger(BrowserService.class);

	public static final String ACTION_EXIT = "EXIT";
	public static final String ACTION_VISITE_PAGE= "VISITE_PAGE";

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
			HashMap<String,Object> map=new HashMap<>();
			map.put("action",BrowserService.ACTION_EXIT);
			ObjectMapper mapper = new ObjectMapper();
			String json=mapper.writeValueAsString(map);
			Socket s = new Socket("127.0.0.1", port);
			s.getOutputStream().write(json.getBytes());
			s.getOutputStream().flush();
			s.getInputStream().close();
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
		}
	}

	public static void processRequest(ServerSocket serverSocket, Socket socket) {
		try {
			byte[] buf = new byte[10240];
			socket.getInputStream().read(buf);
			String json = new String(buf).trim();
			if (json.length() == 0) {
				return;
			}
			log.info(json);

			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,Object> map=mapper.readValue(json,HashMap.class);

			String output = " ";
			if (ACTION_EXIT.equals(map.get("action"))) {
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
			} else if (ACTION_VISITE_PAGE.equals(map.get("action"))) {
				output = BrowserStartup.getBrowser().visitePage(map);
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
