package com.cvnavi.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 每日任务。每日任务可以包含多个班次。每天零时调用newDayBegin()排班。
 * 
 * @author lixy
 *
 */
public abstract class AbstractDailyTask implements Runnable {
	
	static Logger log=LogManager.getLogger(AbstractDailyTask.class);
	
	/**
	 * 每次执行任务时，把下次执行任务的时间计算出来。
	 */
	public long nextRunTime = 0;
	/**
	 * 当天任务完成。
	 */
	boolean todayWorkComplete = false;
	/**
	 * 任务正在运行标记
	 */
	boolean runningFlag = false;
	/**
	 * 计划任务结束标记
	 */
	boolean scheduleCancel = false;
	
	/**
	 *是否允许同时运行多个任务。这里的同时运行，是指多个线程执行同一个任务。
	 */
	boolean allowParallel=false;

	Schedule[] schedules = {};
	
	public Schedule[] emptySchedules = {};
	
	private boolean [] scheduleBeginEventFired;
	
	public abstract Schedule[] initSchedules();

	public AbstractDailyTask() {
		schedules=initSchedules();
	}
	/**
	 * <p>
	 * 新的一天开始。每天零时00:00:00会触发此方法。可以在此方法内安排新一天的任务。
	 * 传到Schedule的参数为08:00:00这种格式，需要转换成unix时间戳。
	 * </p>
	 */
	protected void newDayBegin() {
		todayWorkComplete = false;
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		scheduleBeginEventFired=new boolean[schedules.length];
		
		for (int i = 0; i < schedules.length; i++) {
			scheduleBeginEventFired[i]=false;
			
			Schedule s = schedules[i];
			try {
				s.begin = sdf.parse(date + " " + s.beginTime).getTime();
				s.end = sdf.parse(date + " " + s.endTime).getTime();
			} catch (ParseException e) {
			}
		}
	}

	/**
	 * 时钟每WebBackgroundTaskScheduler.timerPeriod毫秒滴答一次。第次滴答，都要检查是否到达任务执行时间。
	 * 
	 * @param time
	 * @return
	 */
	protected boolean timeToFire(long time) {
		if (scheduleCancel || todayWorkComplete) {
			return false;
		}
		
		if(!allowParallel && runningFlag){
			return false;
		}

		for (int i=0;i<schedules.length;i++) {
			Schedule s =schedules[i];
			if (time >= s.begin && time <= s.end) {
				// System.out.println(time+","+nextRunTime);
				if (time - nextRunTime > s.period) {
					nextRunTime = time;
				}
				if (time >= nextRunTime) {
					nextRunTime += s.period;
					if(scheduleBeginEventFired[i]==false){
						scheduleBeginEventFired[i]=true;
						scheduleBeginEvent(s);
					}
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * 开始执行每个班次时，触发此事件。例如对于这个排班：
	 * 08:00:00-12:00:00,10000;14:00:00-18:00:00,10000;
	 * 08:00:00和14:00:00时会触发此事件
	 * @param s
	 */
	protected void scheduleBeginEvent(Schedule s){
		
	}
	
	public void run(){
		runningFlag=true;
		try{
			doTask();
		}catch(Exception ex){}
		runningFlag=false;
	}
	
	public abstract void doTask();

	public void interruptTask(){

	}

	public void setScheduleCancel(boolean b) {
		this.scheduleCancel = b;
	}

	public void setTodayWorkComplete(boolean b) {
		this.todayWorkComplete = b;
	}
}
