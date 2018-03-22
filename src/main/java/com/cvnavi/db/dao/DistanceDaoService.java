package com.cvnavi.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.cvnavi.distance.PortDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.db.DBConnection;

public class DistanceDaoService {
	static Logger log = LogManager.getLogger(DistanceDaoService.class);

	public static PortDistance getLast() {
		PortDistance pd = new PortDistance();
		try {
			Connection con = DBConnection.getInstance().get();
			if (con != null) {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM port_distance order by id desc limit 1");
				while (rs.next()) {
					pd.id = rs.getInt("id");
					pd.fromPort = rs.getString("from_port");
					pd.toPort = rs.getString("to_port");
					pd.distance = rs.getFloat("distance");
				}
				rs.close();
				st.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
		return pd;
	}

	public static void save(PortDistance pd) {
		try {
			Connection con = DBConnection.getInstance().get();
			if (con != null) {

				PreparedStatement ps = con
						.prepareStatement("insert into port_distance(from_port,to_port,distance) values(?,?,?)");
				ps.setString(1, pd.fromPort);
				ps.setString(2, pd.toPort);
				ps.setFloat(3, pd.distance);
				ps.execute();
				ps.close();

			}
		} catch (Exception e) {
			log.error(e);
		}
	}
}
