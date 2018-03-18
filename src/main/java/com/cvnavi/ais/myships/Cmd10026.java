package com.cvnavi.ais.myships;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cvnavi.ais.model.Track;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import com.cvnavi.util.ZipUtil;

/**
 * myships首页获取轨迹
 * 
 * @author lixy
 *
 */
public class Cmd10026 extends AbstractCmd {
	static Logger log=LogManager.getLogger(Cmd10026.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Override
	public String getCmdUrl(String... args) {
		return "http://www.myships.com/myships/10026";
	}

	@Override
	public List<Track> doCmd(String... args) {
		List<Track> list = null;
		String url = getCmdUrl();
		HashMap<String, String> params = new HashMap<>();
		params.put("mmsi", args[0]);
		params.put("startTime", sdf.format(new Date(Long.parseLong(args[1]) * 1000)));
		params.put("endTime", sdf.format(new Date(Long.parseLong(args[2]) * 1000)));
		Header header = Header.createDefault().referer("http://www.myships.com/myships/");
		String content = HttpUtil.doHttpPost(url, params, header, null,HttpUtil.RANDOM_PROXY);
		try {
			list = parseTrack(content);
		} catch (Exception e) {
			log.error(e);
		}

		return list;
	}

	private List<Track> parseTrack(String content) throws IOException {
		List<Track> list=new ArrayList<Track>();
		
		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(content, Map.class);
		if (((int) map.get("status")) == 0) {
			String data = map.get("data").toString();
			byte[] b = new byte[data.length()];
			for (int i = 0; i < b.length; i++) {
				b[i] = (byte) data.codePointAt(i);
			}
			b = ZipUtil.unZlib(b);
			String result = new String(b);
			List li = mapper.readValue(result, List.class);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(Object item:li){
				Map m=(Map) item;
				Track t=new Track();
				t.la=convertDegree(m.get("lat").toString());
				t.lo=convertDegree(m.get("lon").toString());
				try {
					t.t=""+(sdf.parse(m.get("p").toString()).getTime()/1000 );
				} catch (ParseException e) {
					log.error(e);
				}
				t.c=m.get("c").toString();
				t.s=m.get("s").toString();
				list.add(t);
			}
		}
		return list;
	}

	

	public static void main(String args[]) {
		new Cmd10026().doCmd("412404360", "1482593400", "1482594240");
	}
}
