package com.cvnavi.distance;

import java.util.List;

import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.Schedule;
import com.cvnavi.util.MailSender;
import com.cvnavi.util.ResourceReader;

@WebListener
public class CrawlDistanceTask extends AbstractDailyTask {

	static Logger log = LogManager.getLogger(CrawlDistanceTask.class);

	static List<String> ports = ResourceReader.readLines("/port.txt");

	private long lastRun = 0;
	private String fromPort;
	private String toPort;

	@Override
	public Schedule[] initSchedules() {
		return emptySchedules;
	}

	@Override
	public void doTask() {

		if (System.currentTimeMillis() - lastRun > 1000 * 60 * 20 || DistanceCmd.DSId==null) {
			log.info("Start distance task.");
			if (!DistanceCmd.openSession()) {
				MailSender.sendMail("CrawlDistanceTask error","Can not open marinecircle session.");
				setTodayWorkComplete(true);
				return;
			}
			PortDistance pd = DistanceDao.getLast();
			setNext(pd.fromPort, pd.toPort);
		}
		lastRun=System.currentTimeMillis();

		String distance = DistanceCmd.getDistance(fromPort, toPort);
		if (DistanceCmd.LIMIT.equals(distance)) {
			log.info(distance);
			setTodayWorkComplete(true);
			return;
		}

		float f = 0;
		try {
			f = Float.parseFloat(distance);
			PortDistance pd2 = new PortDistance();
			pd2.fromPort = fromPort;
			pd2.toPort = toPort;
			pd2.distance = f;
			DistanceDao.save(pd2);
			setNext(fromPort, toPort);

			log.info(fromPort + "," + toPort + ":" + distance);
		} catch (Exception ex) {
			log.info("Task cancel.");
			setScheduleCancel(true);
			MailSender.sendMail("Distance task cancel", fromPort + "," + toPort);
			return;
		}
	}

	private void setNext(String p1, String p2) {
		int index1 = ports.indexOf(p1);
		int index2 = ports.indexOf(p2);
		index2 += 1;
		if (index2 >= ports.size()) {
			index1 += 1;
			index2 = index1 + 1;
		}
		fromPort = ports.get(index1);
		toPort = ports.get(index2);
	}
}
