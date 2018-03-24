package com.cvnavi.ais.shipxy;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.ais.model.Track;

import java.util.List;

/**
 * 从shipxy取船位、轨迹信息。具体方法有多种，有不同的实现方式。依次尝试每种方法，确保能获取道数居。
 * @author lixy
 *
 */
public class ShipxyService {
	
	/**
	 * 取船位信息。如果一次要取多条船，多个mmsi之间用逗号分隔。
	 * @param mmsi
	 * @return
	 */
	public static List<Ship> getShip(final String mmsi) {
		List<Ship> list = null;

		if(mmsi.contains(",")){
			list=new Cmd134().doCmd(mmsi);
		} else{
			list=new Cmd130().doCmd(mmsi);
		}
		
		 if(list==null || list.size()==0){
			 list=new Cmd2003().doCmd(mmsi);
		 }
		 
		 if(list==null || list.size()==0){
			 list=new CmdGetDetail().doCmd(mmsi);
		 }
		return list;
	}
	
	/**
	 * 取轨迹信息。
	 * @param mmsi
	 * @return
	 */
	public static List<Track> getTrack(String mmsi,long start,long end) {
		List<Track> list=null;
//		list=new CmdShipTrack().doCmd(mmsi,start+"",end+"");
		list=new Cmd132().doCmd(mmsi,start+"",end+"");
		 if(list==null || list.size()==0){
			 list=new Cmd2005().doCmd(mmsi,start+"",end+"");
		 }
		return list;
	}
}
