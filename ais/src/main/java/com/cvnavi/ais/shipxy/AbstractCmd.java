package com.cvnavi.ais.shipxy;

import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbstractCmd {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public abstract String getCmdUrl(String... args);

	public abstract List<?> doCmd(String... args);

	static String[] shipTypeArray = { "引航船", "搜救船", "拖轮", "港口供应船", "其他", // "装有防污装置和设备的船舶",
			"执法艇", "其他", // "备用-用于当地船舶的任务分配",
			"其他", // "备用-用于当地船舶的任务分配",
			"医疗船", "其他", // "符合18号决议(Mob-83)的船舶",
			"捕捞", "拖引", "拖引", // "拖引并且船长>200m或船宽>25m",
			"疏浚或水下作业", "潜水作业", "参与军事行动", "帆船航行", "娱乐船", "地效应船", "高速船", "客船", "货船", "油轮", "其他", "集装箱船" };

	protected String getShipTypeText(int t) {
		if (t < 10 || t > 100)
			return "";
		int num_10 = (int) Math.floor(t / 10.0);
		int num_1 = t % 10;
		if (num_10 == 5) {
			return shipTypeArray[num_1];
		} else if (num_10 == 3) {
			if (num_1 >= 0 && num_1 <= 7) {
				return shipTypeArray[num_1 + 10];
			}
			return "";
		} else {
			switch (num_10) {
			case 2:
				return shipTypeArray[18];
			case 4:
				return shipTypeArray[19];
			case 6:
				return shipTypeArray[20];
			case 7:
				return shipTypeArray[21];
			case 8:
				return shipTypeArray[22];
			case 9:
				return shipTypeArray[23];
			case 10:
				return shipTypeArray[24];
			default:
				return "";
			}
		}
	}

	private static String[] shipStatus = { "在航(主机推动)", "锚泊", "失控", "操作受限", "吃水受限", "靠泊", "搁浅", "捕捞作业", "靠船帆提供动力" };

	protected String getStatusText(int t) {
		if (t >= 0 && t <= 8) {
			return shipStatus[t];
		}
		return "";
	}
}
