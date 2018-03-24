package com.cvnavi.ais.myships;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * myships首页单船船位
 * @author lixy
 *
 */
public class Cmd10025 extends AbstractCmd {
	static Logger log=LogManager.getLogger(Cmd10025.class);
	
	@Override
	public String getCmdUrl(String... args) {
		return "http://www.myships.com/myships/10025?mmsi=" + args[0];
	}

	@Override
	public List<Ship> doCmd(String... args) {
		List<Ship> list = null;
		String url = getCmdUrl(args[0]);
		Header header = Header.createRandom().referer("http://www.myships.com/");
		String content = HttpUtil.doHttpGet(url, header, null,HttpUtil.RANDOM_PROXY);
		try {
			list = parseShip(content);
		} catch (Exception e) {
			log.error(e);
		}

		return list;
	}

	protected List<Ship> parseShip(String content) throws JsonParseException, JsonMappingException, IOException {

		List<Ship> list = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(content, Map.class);
		if (((int) map.get("status")) == 0) {
			Map m= (Map) map.get("data");
			list.add(parseSingleShip(m));
		}
		return list;
	}
	

	private Ship parseSingleShip(Map map) {
		Ship s = new Ship();
		s.Name = map.get("enName").toString();
		s.MMSI = map.get("mmsi").toString();
		s.CallSign = map.get("callsign").toString();
		s.IMO = map.get("imo").toString();
		s.ShipType=map.get("shipTypeName").toString();
		s.ShipTypeStr = getShipTypeText(map.get("shipTypeName").toString());
		s.Status = getStatusText(map.get("nav_status_name").toString());
		s.Length = String.format("%.1f",(double) map.get("length")) + "米";// 船长
		s.Width = String.format("%.1f", (double) map.get("breadth")) + "米";// 船宽
		s.Draught = map.get("draught") + "米";// 吃水
		if(s.Draught.contains("N/A")||s.Draught.contains("null")){
			s.Draught="";
		}

		s.LatitudeStr = map.get("lat").toString().replaceAll("\"", "");
		s.Latitude=convertDegree(s.LatitudeStr);
		
		s.LongitudeStr = map.get("lon").toString().replaceAll("\"", "");
		s.Longitude=convertDegree(s.LongitudeStr);
		
		s.Speed = map.get("speed")+ "节";

		s.ETA = map.get("eta").toString();// 预到时间
		s.LastTime = map.get("updateTimeStr").toString();// 最后时间，更新时间
//		s.SatTime = sdf.format(new Date(Long.parseLong(map.get("laststa").toString()) * 1000));// 卫星时间

		s.Heading =map.get("heading") + "度";// 船首向
		if(s.Heading.contains("N/A")||s.Heading.contains("null")){
			s.Heading="";
		}
		s.Course =map.get("course") + "度";// 航迹向
		if(s.Course.contains("N/A")||s.Course.contains("null")){
			s.Course="";
		}
		s.Dest = map.get("dest_port").toString();// 目的地
		return s;
	}
	
	 private String getShipTypeText(String id){
		String url="http://www.myships.com/myships/36001?type=ais_ship_type_id&id="+id;
		String content=HttpUtil.doHttpGet(url);
		ObjectMapper mapper = new ObjectMapper();
		Map map=null;
		try {
			map = mapper.readValue(content, Map.class);
		} catch (IOException e) {
			log.error(e);
		}
		if(map!=null){
			Map m= (Map) map.get("data");
			if(m!=null){
				return m.get("chsName").toString();
			}
		}
		return "";
	}
	 
	 private String getStatusText(String id){
			String url="http://www.myships.com/myships/36001?type=nav_status_id&id="+id;
			String content=HttpUtil.doHttpGet(url);
			ObjectMapper mapper = new ObjectMapper();
			Map map=null;
			try {
				map = mapper.readValue(content, Map.class);
			} catch (IOException e) {
				log.error(e);
			}
			if(map!=null){
				Map m= (Map) map.get("data");
				if(m!=null){
					return m.get("chsName").toString();
				}
			}
			return "";
		}

	public static void main(String args[]) {
		new Cmd10025().doCmd("414122000");
	}
}
