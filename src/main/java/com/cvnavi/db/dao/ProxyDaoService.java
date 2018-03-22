package com.cvnavi.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.cvnavi.db.DBConnection;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 将代理保存到数据库，或从数据库加载代理数据。
 * 
 * @author lixy
 *
 */
public class ProxyDaoService {
	static Logger log = LogManager.getLogger(ProxyDaoService.class);

	private static Set<HttpHost> aliveProxies = Collections.synchronizedSet(new HashSet<HttpHost>());

	/**
	 * 从有效的代理中随机取一个代理。
	 * 
	 * @return
	 */
	public static HttpHost getRandomProxy() {
		if (aliveProxies.size() > 0) {
			int i = new Random().nextInt(aliveProxies.size());
			HttpHost[] temp = new HttpHost[0];
			temp = aliveProxies.toArray(temp);
			return temp[i];
		}
		return null;
	}

	public static void saveAliveProxy(Collection<HttpHost> c) {
		if (c.size() == 0) {
			return;
		}
		try {
			Connection con = DBConnection.getInstance().get();
			if (con != null) {
				Collection<HttpHost> inDb=  loadAliveProxy();
				c.removeAll(inDb);

				PreparedStatement stmt = con.prepareStatement("insert into alive_proxy(proxy) values(?)");
				for (HttpHost proxy : c) {
					stmt.setString(1, proxy.toString());
					stmt.addBatch();
				}
				stmt.executeBatch();
				stmt.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public static synchronized void deleteAliveProxy(Collection<HttpHost> proxies) {
		if (proxies.size() == 0) {
			return;
		}
		try {
			Connection con = DBConnection.getInstance().get();
			if (con != null) {
				PreparedStatement ps = con.prepareStatement("delete  from alive_proxy where proxy=?");
				for (HttpHost proxy : proxies) {
					ps.setString(1, proxy.toString());
					ps.addBatch();
				}
				ps.executeBatch();
				ps.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public static Collection<HttpHost> loadAliveProxy() {
		Set<HttpHost> set = Collections.synchronizedSet(new HashSet<HttpHost>());
		try {
			Connection con = DBConnection.getInstance().get();
			if (con != null) {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select * from alive_proxy");
				while (rs.next()) {
					String s = rs.getString("proxy");
					set.add(HttpHost.create(s));
				}
				rs.close();
				st.close();
			}
		} catch (Exception e) {
			log.error(e);
		}

		aliveProxies = set;

		return set;
	}

	public static Collection<HttpHost> getAliveProxies() {
		return aliveProxies;
	}

}
