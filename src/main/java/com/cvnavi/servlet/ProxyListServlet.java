package com.cvnavi.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cvnavi.db.dao.ProxyDaoService;
import org.apache.http.HttpHost;


/**
 * Servlet implementation class ProxyListServlet
 */
@WebServlet("/proxy")
public class ProxyListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Collection<HttpHost> all = ProxyDaoService.loadAliveProxy();
		StringBuilder sb=new StringBuilder();
		sb.append("<!Doctype html><html><head><meta http-equiv=Content-Type content=\"text/html;charset=utf-8\"></head><body>");
		sb.append("total count:"+all.size()).append("<br/>");
		for(HttpHost host:all) {
			sb.append(host.toHostString()).append("<br/>");
		}
		sb.append("</body></html>");
		response.getWriter().append(sb.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
