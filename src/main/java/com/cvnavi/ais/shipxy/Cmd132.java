package com.cvnavi.ais.shipxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.ais.model.Track;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import com.cvnavi.util.LEDataInputStream;

/**
 * 通过shipxy首页获取轨迹
 * @author lixy
 *
 */
public class Cmd132 extends AbstractCmd {

	static Logger log=LogManager.getLogger(Cmd132.class);
	
	public String getCmdUrl(String... args) {
		String url = "http://shipdll.shipxy.com/dll/dp.dll?enc=1&cmd=132&tp=1dpid=&scode=%1s&shipid=%2s&btime=%3s&etime=%4s";
		return String.format(url, args[0], args[1], args[2], args[3]);
	}

	public List<Track> doCmd(String... args) {

		List<Track> list = null;
		String scode = ShipxyHeartbeat.getScode();
		if (scode != null) {
			String url = getCmdUrl(scode,args[0],args[1],args[2]);
			Header header=Header.createRandom().referer("http://www.shipxy.com/");
			header.put("X-Requested-With", "ShockwaveFlash/23.0.0.207");
			String content = HttpUtil.doHttpGet(url, header,null,HttpUtil.RANDOM_PROXY);
			try {
				byte[] b = Base64.getDecoder().decode(content);
				list = parseTrack(b);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
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
		if(course<0){
			course+=65536;
		}
		t.c = String.format("%.1f", course / 100.0);
		return t;
	}

}
