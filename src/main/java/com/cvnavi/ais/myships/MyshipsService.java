package com.cvnavi.ais.myships;

import java.util.List;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.ais.model.Track;

/**
 * 从myships取船位、轨迹信息。具体方法有多种，有不同的实现方式。依次尝试每种方法，确保能获取道数居。
 * @author lixy
 *
 */
public class MyshipsService {
	
	/**
	 * 取船位信息。如果一次要取多条船，多个mmsi之间用逗号分隔。
	 * @param mmsi
	 * @return
	 */
	public static List<Ship> getShip(String mmsi) {
		List<Ship> list = null;

		list=new Cmd10025().doCmd(mmsi);
		return list;
	}
	
	/**
	 * 取轨迹信息。
	 * @param mmsi
	 * @return
	 */
	public static List<Track> getTrack(String mmsi,long start,long end) {
		List<Track> list=null;
		list=new Cmd10026().doCmd(mmsi,start+"",end+"");
		return list;
	}
}
