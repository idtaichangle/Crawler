package com.cvnavi.ship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.db.DBConnection;

/**
 * 将代理保存到数据库，或从数据库加载代理数据。
 * 
 * @author lixy
 *
 */
public class ShipDao {
	static Logger log = LogManager.getLogger(ShipDao.class);

	public synchronized static void saveShips(Collection<Ship> c) {
		if (c.size() == 0) {
			return;
		}
		try {
			Connection con = DBConnection.get();
			if (con != null) {
				List<Ship> list1=new ArrayList<>();
				List<Ship> list2=new ArrayList<>();
				for(Ship s :c){
					if(s.crawled==1){
						list1.add(s);
					}
					if(s.crawled==2){
						list2.add(s);
					}
				}
				
				String sql = "update shipxy_ship set name_en=? ,imo=?,call_sign=?,length=?,breadth=?,draught=?,type=?,type_str=?,crawled=? where mmsi=?";
				PreparedStatement stmt = con.prepareStatement(sql);
				for (Ship ship : list1) {
					stmt.setString(1, ship.name_en);
					stmt.setString(2, ship.imo);
					stmt.setString(3, ship.call_sign);
					stmt.setFloat(4, ship.length);
					stmt.setFloat(5, ship.breadth);
					stmt.setFloat(6, ship.draught);
					stmt.setInt(7, ship.type);
					stmt.setString(8, ship.typeStr);
					stmt.setInt(9, ship.crawled);
					stmt.setString(10, ship.mmsi);
					stmt.addBatch();
				}
				if(list1.size()>0){
					stmt.executeBatch();
				}
				
				sql = "update shipxy_ship set crawled=? where mmsi=?";
				stmt = con.prepareStatement(sql);
				for (Ship ship : list2) {
					stmt.setInt(1, ship.crawled);
					stmt.setString(2, ship.mmsi);
					stmt.addBatch();
				}
				if(list2.size()>0){
					stmt.executeBatch();
				}
				
				stmt.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public static List<Ship> loadShips() {
		List<Ship> list = Collections.synchronizedList(new ArrayList<Ship>());
		try {
			Connection con = DBConnection.get();
			if (con != null) {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id,mmsi from shipxy_ship where crawled=0 order by id limit 90");
				while (rs.next()) {
					Ship s = new Ship();
					s.id=rs.getInt("id");
					s.mmsi = rs.getString("mmsi");
					list.add(s);
				}
				rs.close();
				st.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
		return list;
	}
}
