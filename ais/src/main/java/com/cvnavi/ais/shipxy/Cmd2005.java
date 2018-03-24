package com.cvnavi.ais.shipxy;

import com.cvnavi.ais.model.Track;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import com.cvnavi.util.LEDataInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 通过http://api.shipxy.com/api/demo/获取轨迹
 * 
 * @author lixy
 *
 */
public class Cmd2005 extends AbstractCmd {

	static Logger log=LogManager.getLogger(Cmd2005.class);
	
	public String getCmdUrl(String... args) {
		String url = "http://api.shipxy.com/apicall/index?v=2&k=1F6D701272402D1E7D8D316CCE519123&enc=0&cmd=2005&cut=1&id=%1s&btm=%2s&etm=%3s";
		return String.format(url, args[0], args[1], args[2]);
	}

	public List<Track> doCmd(String... args) {

		List<Track> list = null;
		String url = getCmdUrl(args[0], args[1], args[2]);
		Header header=Header.createRandom().referer("http://api.shipxy.com/api/demo/");
		String content = HttpUtil.doHttpGet(url, header,null,HttpUtil.RANDOM_PROXY);
		try {
			byte[] b = Base64.getDecoder().decode(content);
			list = parseTrack(b);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return list;
	}

	protected List<Track> parseTrack(byte[] bytes) throws IOException {
		if (bytes == null || bytes[0] == 0 || bytes.length < 20) {
			return null;
		}

		LEDataInputStream dis = new LEDataInputStream(new ByteArrayInputStream(bytes));
		dis.readInt();//dataLength
		int status = dis.readShort();
		int count = dis.readInt();
		if (status != 0) {
			return null;
		}

		Track[] result = new Track[count];

		for (int i = 0; i < count; i++) {
			result[i] = parseSingleTrack(dis);
		}
		dis.readShort();// continue 轨迹数据太多，可能一次返回不完全。
		return Arrays.asList(result);
	}

	protected Track parseSingleTrack(LEDataInputStream dis) throws IOException {
		Track t = new Track();
		t.t = dis.readLong() + "";
		dis.readShort();//from 数据类型
		t.lo = "" + (dis.readInt() / 1000000.0);
		t.la = "" + (dis.readInt() / 1000000.0);
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
