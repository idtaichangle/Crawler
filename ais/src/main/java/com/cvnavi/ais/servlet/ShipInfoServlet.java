package com.cvnavi.ais.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.cvnavi.ais.model.Ship;
import com.cvnavi.ais.myships.MyshipsService;
import com.cvnavi.ais.shipxy.ShipxyService;

/**
 * Servlet implementation class ShipInfoServlet
 */
@WebServlet("/shipinfo")
public class ShipInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String mmsi = request.getParameter("mmsi");
		List<Ship> ship = ShipxyService.getShip(mmsi);
		if (ship==null || ship.size()==0) {
			ship = MyshipsService.getShip(mmsi);
		}
		
		PrintWriter out = response.getWriter();
		if (ship!=null && ship.size()>0) {
			ObjectMapper mapper = new ObjectMapper();
			String s=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ship);
			
			out.println("{\"success\":true,\"message\":\"操作成功。\",\"data\":"+s+"}");
		} else {
			out.println("{\"success\":false,\"message\":\"操作失敗。\"}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
