package com.cvnavi.ais.shipxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.util.HttpUtil;

/**
 * 通过shipxy手机接口获取船位,可以是单船或多船
 * 
 * @author lixy
 *
 */
public class CmdGetDetail extends AbstractCmd {

	static Logger log=LogManager.getLogger(CmdGetDetail.class);
	
	public String getCmdUrl(String... args) {
		String url = "http://open.shipxy.com//ship/getDetails?src=0&enc=1&shipid=%1s";
		return String.format(url, args[0]);
	}

	public List<Ship> doCmd(String... args) {

		List<Ship> list = null;

		String url = getCmdUrl(args[0]);
		String content = HttpUtil.doHttpPost(url,(HashMap<String, String>)null,null,null,HttpUtil.RANDOM_PROXY,5000,Level.INFO);
		try {
			list = parseShip(content);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return list;
	}

	protected List<Ship> parseShip(String content) throws JsonParseException, JsonMappingException, IOException {

		List<Ship> list = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		Map<?,?> map = mapper.readValue(content, Map.class);
		if (((Integer) map.get("status")) == 0) {
			List<Map<?,?>> maps = (List<Map<?,?>>) map.get("data");
			for (Map<?,?> m : maps) {
				list.add(parseSingleShip(m));
			}
		}
		return list;
	}

	private Ship parseSingleShip(Map<?,?> map) {
		Ship s = new Ship();
		s.Name = map.get("name").toString();
		s.MMSI = map.get("shipid").toString();
		s.CallSign = map.get("callsign").toString();
		s.IMO = map.get("imo").toString();
		s.ShipType = map.get("type").toString();
		s.ShipTypeStr = getShipTypeText((Integer) map.get("type"));
		s.Status = getStatusText((Integer) map.get("navistatus"));
		s.Length = String.format("%.1f", ((Integer) map.get("length")) / 10.0) + "米";// 船长
		s.Width = String.format("%.1f", ((Integer) map.get("width")) / 10.0) + "米";// 船宽
		s.Draught = String.format("%.1f", ((Integer) map.get("draught")) / 1000.0) + "米";// 吃水

		double lat =Long.parseLong(map.get("lat").toString())/ 1000000.0;
		s.Latitude = lat + "";
		double fra = lat - ((int) lat);
		s.LatitudeStr = ((int) lat) + "-" + String.format("%.3fN", fra * 60);// 如何判断北纬南纬？

		double lon =Long.parseLong(map.get("lon").toString())/ 1000000.0;
		s.Longitude = lon + "";
		fra = lon - ((int) lon);
		s.LongitudeStr = ((int) lon) + "-" + String.format("%.3fN", fra * 60);// 如何判断北纬南纬？

		int speed = (Integer) map.get("sog");
		if (speed >= 52576 || speed < 0) {
			s.Speed = "";
		} else {
			s.Speed = String.format("%.1f", (speed / 514.0)) + "节";
		}

		s.ETA = map.get("eta").toString();// 预到时间
		s.LastTime = sdf.format(new Date(Long.parseLong(map.get("lastdyn").toString()) * 1000));// 最后时间，更新时间
		s.SatTime = sdf.format(new Date(Long.parseLong(map.get("laststa").toString()) * 1000));// 卫星时间

		int heading = (Integer) map.get("hdg");
		if (heading < 0) {
			heading = heading + 65536;
		}
		if (heading == 51100) {
			heading = 0;
		}
		if (heading > 0) {
			s.Heading = String.format("%.1f", heading / 100.0) + "度";// 船首向
		} else {
			s.Heading = "未知";
		}

		int course = (Integer) map.get("cog");
		if (course < 0) {
			course = course + 65536;
		}
		s.Course = String.format("%.1f", course / 100.0) + "度";// 航迹向
		s.Dest = map.get("dest").toString();// 目的地
		return s;
	}

	public static void main(String args[]) {
		new CmdGetDetail().doCmd("414122000");
	}

}
