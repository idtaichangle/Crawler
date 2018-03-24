package com.cvnavi.ais.shipxy;

import com.cvnavi.ais.model.Track;
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
 * 通过shipxy（登录后）首页获取轨迹。
 * @author lixy
 *
 */
public class CmdShipTrack extends AbstractCmd {

	static Logger log=LogManager.getLogger(CmdShipTrack.class);
	
	
	public String getCmdUrl(String... args) {
		String url = "http://www.shipxy.com/ship/track?shipid=%1s&btime=%2s&etime=%3s";
		return String.format(url, args[0], args[1], args[2]);
	}

	public List<Track> doCmd(String... args) {

		List<Track> list = null;
		String url = getCmdUrl(args[0],args[1],args[2]);
		Header header=Header.createRandom().referer("http://www.shipxy.com/");
		header.put("X-Requested-With", "ShockwaveFlash/23.0.0.207");
		
		HashMap<String, String> cookie = new HashMap<>();
		cookie.put("ASP.NET_SessionId", ShipxyHeartbeat.getSessionId());
		cookie.put(".UserAuth2", ShipxyHeartbeat.getUserAuth());
		
		String content = HttpUtil.doHttpGet(url, header,cookie,HttpUtil.RANDOM_PROXY);
		try {
			byte[] b = Base64.getDecoder().decode(content);
			list = parseTrack(b);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return list;
	}

	private List<Track> parseTrack(byte[] bytes) throws IOException {
		if (bytes == null || bytes[0] == 0 || bytes.length < 20) {
			return null;
		}
		LEDataInputStream dis = new LEDataInputStream(new ByteArrayInputStream(bytes));
		int dataLength = dis.readInt();
		int status = dis.readShort();
		if (status == 0 && dataLength > 0) {
			List<Track> list = new ArrayList<Track>();

			long position = 6;
			while (position < bytes.length - 21) {
				Track t = parseSingleTrack(dis);
				position += 21;
				list.add(t);
			}
			dis.readByte();// continue
			return list;
		}

		return null;
	}

	public Track parseSingleTrack(LEDataInputStream dis) throws IOException {
		Track t = new Track();
		t.t = dis.readLong() + "";
		dis.readByte();// from
		t.la = "" + (dis.readInt() / 1000000.0);
		t.lo = "" + (dis.readInt() / 1000000.0);
		int speed = dis.readShort();
		if (speed > 52576) {
			t.s = "";
		} else {
			t.s = String.format("%.1f", (speed / 514.0));
		}
		int course = dis.readShort();
		t.c = String.format("%.1f", course / 100.0);
		return t;
	}

}
