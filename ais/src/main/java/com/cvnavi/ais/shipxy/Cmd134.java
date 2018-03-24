package com.cvnavi.ais.shipxy;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import com.cvnavi.util.LEDataInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * 通过shipxy首页获取多船船位(需要登录)
 * 
 * @author lixy
 *
 */
public class Cmd134 extends Cmd130 {

	static Logger log = LogManager.getLogger(Cmd134.class);

	public String getCmdUrl(String... args) {
		// 登录后可以使用http://shipdll.shipxy.com/dll/dp.dll?cmd=134
		// freedll.shipxy.com不支持cmd=134
		String url = "http://shipdll.shipxy.com/dll/dp.dll?cmd=134&scode=%1s&enc=1&shipid=%2s";
		return String.format(url, args[0], args[1]);
	}

	public List<Ship> doCmd(String... args) {

		List<Ship> list = null;
		
		if (ShipxyHeartbeat.getScode()!=null) {
			String url = getCmdUrl(ShipxyHeartbeat.getScode(), args[0]);

			Header header = Header.createRandom().referer("http://www.shipxy.com/");
			header.put("X-Requested-With", "ShockwaveFlash/17.0.0.169");

			HashMap<String, String> cookie = new HashMap<>();
			cookie.put("ASP.NET_SessionId", ShipxyHeartbeat.getSessionId());
			cookie.put(".UserAuth2", ShipxyHeartbeat.getUserAuth());
			String content = HttpUtil.doHttpGet(url, header, cookie,HttpUtil.RANDOM_PROXY);
			if (!content.contains("status")) {// 正常情况返回base64字符串。如果登录无效，返回{status:98}
				try {
					byte[] b = Base64.getDecoder().decode(content);
					list = parseShip(b);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
		if(list==null||list.size()==0){
			new Thread(){
				@Override
				public void run() {
					ShipxyHeartbeat.refreshScode();
				}
			}.start();
		}
		return list;
	}

	protected List<Ship> parseShip(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length == 0 || bytes[0] == 0) {
			return null;
		}

		LEDataInputStream dis = new LEDataInputStream(new ByteArrayInputStream(bytes));
		dis.readInt();// dataLength
		int status = dis.readShort();
		int count = dis.readInt();
		if (status != 0) {
			return null;
		}

		List<Ship> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(parseSingleShip(dis));
			dis.readByte();
		}
		return list;
	}
}
