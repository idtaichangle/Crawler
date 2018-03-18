package com.cvnavi.task;

/**
 * <p>
 * 每天的计划任务。一天可以有多个计划。
 * 初始化计划时，传入时间段，以及该时间段内的执行任务的时间间隔(单位为毫秒)。
 * 例如08:00:00-18:00:00,1000
 * </p>
 * 
 * @author lixy
 *
 */
public class Schedule {
	long begin = 0;
	long end = 0;
	long period = 1000;
	String beginTime;
	String endTime;

	/**
	 * 创建每日计划。
	 * @param beginTime 每日计划的开始时间，例如08:00:00
	 * @param endTime 每日计划的结束时间，例如23:00:00
	 * @param period  每日计划的执行周期，单位毫秒。
	 */
	public Schedule(String beginTime, String endTime, long period) {
		super();
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.period = period;
	}

	public static Schedule[] parse(String s) {
		if (s != null && s.length() > 0) {
			String array[] = s.split(";");
			Schedule ss[] = new Schedule[array.length];
			for (int i = 0; i < array.length; i++) {
				String item = array[i];
				String from = item.substring(0, item.indexOf("-"));
				String to = item.substring(item.indexOf("-") + 1, item.indexOf(","));
				int period = Integer.parseInt(item.substring(item.indexOf(",") + 1));
				ss[i] = new Schedule(from, to, period);
			}
			return ss;
		}
		return new Schedule[0];
	}
}
