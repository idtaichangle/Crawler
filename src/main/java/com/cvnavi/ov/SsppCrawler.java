package com.cvnavi.ov;

import java.util.HashMap;

import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.Schedule;
import com.cvnavi.util.Header;
import com.cvnavi.util.HttpUtil;

public class SsppCrawler extends AbstractDailyTask {

	public static void main(String[] args) {
		new SsppCrawler().doTask();
	}

	@Override
	public Schedule[] initSchedules() {
		return emptySchedules;
	}

	@Override
	public void doTask() {
		String url = "https://www.sspp.co/json/shipment.aspx";
		Header header = Header.createDefault().referer("https://www.sspp.co/shipment.html");

		HashMap<String, String> params = new HashMap<>();
		params.put("act", "shipment");
		params.put("pagesize", "20");
		params.put("pagenumber", "1");
		params.put("ship_port", "");
		params.put("ship_area", "");
		params.put("shipName", "");
		params.put("startDate", "");
		params.put("endDate", "");
		params.put("shipTong", "");
		params.put("typeSort", "");
		params.put("useSort", "");
		String s = HttpUtil.doHttpPost(url, params, header, null);
		System.out.println(s);
	}

}
