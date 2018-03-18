package com.cvnavi.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cvnavi.distance.CrawlDistanceTask;
import com.cvnavi.ship.ShipxyShipInfo;
import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.WebBackgroundTaskScheduler;

/**
 * Servlet implementation class DistanceServlet
 */
@WebServlet("/control")
public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String task = request.getParameter("task");
		String op = request.getParameter("op");
		if (task != null && op != null) {
			if (task.equals("distance") && op.equals("start")) {
				out.append("start distance");
				AbstractDailyTask t = WebBackgroundTaskScheduler.getTaskByName(CrawlDistanceTask.class.getName());
				if (t != null) {
					t.setScheduleCancel(false);
				}
			} else if (task.equals("shipxyshipinfo") && op.equals("start")) {
				out.append("start distance");
				AbstractDailyTask t = WebBackgroundTaskScheduler.getTaskByName(ShipxyShipInfo.class.getName());
				if (t != null) {
					t.setScheduleCancel(false);
				}
			}
		}
		out.append(" ");
	}
}
