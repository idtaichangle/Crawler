package com.cvnavi.ais.shipxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
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
 * 通过http://api.shipxy.com/api/demo/获取船位,可以是单船或多船的船位。
 * 
 * @author lixy
 *
 */
public class Cmd2003 extends AbstractCmd {

	static Logger log = LogManager.getLogger(Cmd2003.class);

	public String getCmdUrl(String... args) {
		String url = "http://api.shipxy.com/apicall/index?v=2&k=1F6D701272402D1E7D8D316CCE519123&enc=0&cmd=2003&idtype=0&id=%s";
		return String.format(url, args[0]);
	}

	@Override
	public List<Ship> doCmd(String... args) {

		List<Ship> list = null;
		String url = getCmdUrl(args[0]);

		Header header = Header.createRandom().referer("http://api.shipxy.com/api/demo/");
		String content = HttpUtil.doHttpGet(url, header, null,HttpUtil.RANDOM_PROXY);
		try {
			byte[] b = Base64.getDecoder().decode(content);
			list = parseShip(b);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return list;
	}

	protected List<Ship> parseShip(byte[] bytes) throws IOException {
		if (bytes == null || bytes[0] == 0 || bytes.length < 20) {
			return null;
		}

		LEDataInputStream dis = new LEDataInputStream(new ByteArrayInputStream(bytes));
		dis.readInt();// dataLength
		int status = dis.readShort();// status
		dis.readInt();// version
		int count = dis.readInt();
		if (status != 0) {
			return null;
		}

		Ship[] result = new Ship[count];

		for (int i = 0; i < count; i++) {
			result[i] = parseSingleShip(dis);
		}

		return Arrays.asList(result);
	}

	protected Ship parseSingleShip(LEDataInputStream dis) throws IOException {
		Ship ship = new Ship();
		dis.readLong();// shipid
		dis.readInt();
		ship.MMSI = dis.readInt() + "";

		int shipType = dis.readShort();
		ship.ShipTypeStr = getShipTypeText(shipType);

		int imo = dis.readInt();

		if (imo == 0 || imo == 2147483647) {
			ship.IMO = "";
		} else {
			ship.IMO = imo + "";
		}

		int stringLength = dis.readShort();
		byte[] sb = new byte[stringLength];
		dis.readFully(sb);
		ship.Name = new String(sb);
		stringLength = dis.readShort();
		sb = new byte[stringLength];
		dis.readFully(sb);
		ship.CallSign = new String(sb);

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
		dis.readShort();// left
		dis.readShort();// trail
		double draught = dis.readShort() / 1000.0;
		if (draught > 0) {
			ship.Draught = String.format("%.1f", draught) + "米";
		} else {
			ship.Draught = "";
		}

		stringLength = dis.readShort();
		sb = new byte[stringLength];
		dis.readFully(sb);
		ship.Dest = new String(sb);

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

		int shipStatus = dis.readShort();
		ship.Status = getStatusText(shipStatus);

		double lat = dis.readInt() / 1000000.0;
		ship.Latitude = lat + "";
		double fra = lat - ((int) lat);
		ship.LatitudeStr = ((int) lat) + "-" + String.format("%.3fN", fra * 60);// 如何判断北纬南纬？

		double lon = dis.readInt() / 1000000.0;
		ship.Longitude = lon + "";
		fra = lon - ((int) lon);
		ship.LongitudeStr = ((int) lon) + "-" + String.format("%.3fE", fra * 60);// 如何判断东经西经？

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

		int course = dis.readShort();
		if (course < 0) {
			course = course + 65536;
		}
		ship.Course = String.format("%.1f", course / 100.0) + "度";

		int speed = dis.readShort();
		if (speed >= 52576 || speed < 0) {
			ship.Speed = "";
		} else {
			ship.Speed = String.format("%.1f", (speed / 514.0)) + "节";
		}

		dis.readShort();// rot

		long time = dis.readLong();// time;
		ship.LastTime = sdf.format(new Date(time * 1000));

		return ship;
	}

}
