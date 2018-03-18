package com.cvnavi.ais.shipxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;
import com.cvnavi.util.LEDataInputStream;

/**
 * 通过shipxy首页获取单船船位。
 * 
 * @author lixy
 *
 */
public class Cmd130 extends AbstractCmd {
	
	static Logger log=LogManager.getLogger(Cmd130.class);
	
	public String getCmdUrl(String... args) {
		//登录后，只能使用shipdll.shipxy.com
		String url= "http://shipdll.shipxy.com/dll/dp.dll?cmd=130&enc=1&scode=%1s&shipid=%2s";
		//未登录时，只能使用freedll.shipxy.com
//		String url = "http://freedll.shipxy.com/dll/dp.dll?cmd=130&enc=1&scode=%1s&shipid=%2s";
		return String.format(url, args[0], args[1]);
	}

	public List<Ship> doCmd(String... args) {

		List<Ship> list = null;
		String scode = ShipxyHeartbeat.getScode();

		if (scode != null) {
			String url = getCmdUrl(scode, args[0]);
			Header header = Header.createRandom().referer("http://www.shipxy.com/");
			String content = HttpUtil.doHttpGet(url, header, null,HttpUtil.RANDOM_PROXY);
			try {
				byte[] b = Base64.getDecoder().decode(content);
				list = parseShip(b);
			} catch (Exception e) {
				log.error(e.getMessage());
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
		dis.readInt();//dataLength
		int status = dis.readShort();

		if (status != 0) {
			return null;
		}

		List<Ship> list = new ArrayList<>();
		list.add(parseSingleShip(dis));
		return list;
	}

	/**
	 * 解析一条船。如果结果是多条船，循环调用此方法即可。（所以LEDataInputStream先要把信息头读取掉）
	 * 
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	protected Ship parseSingleShip(LEDataInputStream dis) throws IOException {
		Ship ship = new Ship();
		Long time = dis.readLong();
		ship.SatTime = sdf.format(new Date(time * 1000));
		ship.MMSI = dis.readInt() + "";

		dis.readInt();// 0,0,0,0

		dis.readByte();// 0

		time = dis.readLong();// time;
		ship.LastTime = sdf.format(new Date(time * 1000));

		double lat = dis.readInt() / 1000000.0;
		ship.Latitude = lat + "";
		double fra = lat - ((int) lat);
		ship.LatitudeStr = ((int) lat) + "-" + String.format("%.3fN", fra * 60);// 如何判断北纬南纬？

		double lon = dis.readInt() / 1000000.0;
		ship.Longitude = lon + "";
		fra = lon - ((int) lon);
		ship.LongitudeStr = ((int) lon) + "-" + String.format("%.3fE", fra * 60);// 如何判断东经西经？

		int speed = dis.readShort();
		if (speed >= 52576 || speed < 0) {
			ship.Speed = "";
		} else {
			ship.Speed = String.format("%.1f", (speed / 514.0)) + "节";
		}
		// byte[] temp = br.readBytes(2);
		int course = dis.readShort();
		if (course < 0) {
			course = course + 65536;
		}
		ship.Course = String.format("%.1f", course / 100.0) + "度";

		int heading = dis.readShort();
		if (heading < 0) {
			heading = heading + 65536;
		}
		if (heading == 51100) {
			heading = 0;
		}
		if (heading > 0) {
			ship.Heading = String.format("%.1f", heading / 100.0) + "度";
		} else {
			ship.Heading = "未知";
		}

		dis.readShort();// what

		byte shipStatus = dis.readByte();
		ship.Status = getStatusText(shipStatus);

		dis.readInt();//mmsi
		dis.readByte();// 0

		byte shipType = dis.readByte();
		ship.ShipType=shipType+"";
		ship.ShipTypeStr = getShipTypeText(shipType);

		float shipLength = (float) (dis.readShort() / 10.0);
		shipLength = shipLength > 511 ? 511 : shipLength;
		if (shipLength > 0) {
			ship.Length = String.format("%.1f", shipLength) + "米";
		} else {
			ship.Length = "";
		}
		double shipWidth = dis.readShort() / 10.0;
		if (shipWidth > 0) {
			ship.Width = String.format("%.1f", shipWidth) + "米";
		} else {
			ship.Width = "";
		}

		dis.readInt();// what
		int imo = dis.readInt();

		if (imo == 0 || imo == 2147483647) {
			ship.IMO = "";
		} else {
			ship.IMO = imo + "";
		}

		int stringLength = dis.readShort();
		byte[] sb = new byte[stringLength];
		dis.readFully(sb);
		ship.CallSign = new String(sb);

		stringLength = dis.readShort();
		sb = new byte[stringLength];
		dis.readFully(sb);
		ship.Name = new String(sb);
		double draught = dis.readShort() / 1000.0;
		if (draught > 0) {
			ship.Draught = String.format("%.1f", draught) + "米";
		} else {
			ship.Draught = "";
		}

		byte month = dis.readByte();
		byte day = dis.readByte();
		byte hour = dis.readByte();
		byte minute = dis.readByte();

		if (month == 0 && day == 0 && hour == 0 && minute == 0) {
			ship.ETA = "";
		} else {
			ship.ETA = month + "." + day + " " + (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "")
					+ minute;
		}

		stringLength = dis.readShort();
		sb = new byte[stringLength];
		dis.readFully(sb);
		ship.Dest = new String(sb,"GBK").trim();
		return ship;
	}

}
