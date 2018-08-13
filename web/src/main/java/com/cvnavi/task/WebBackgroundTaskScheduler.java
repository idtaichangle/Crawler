package com.cvnavi.task;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.util.ResourceReader;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.SubclassMatchProcessor;

/**
 * web后台任务调度。
 * 
 * @author lixy
 *
 */
public class WebBackgroundTaskScheduler implements Runnable{

	static Logger log=LogManager.getLogger(WebBackgroundTaskScheduler.class);
	
	static List<AbstractDailyTask> tasks;
	public static long timerPeriod = 100;
//	protected Timer timer;
	protected long tomorrow;// 明天零时00:00:00。在这个时刻，需要重新计算每个任务的当天排班。
	protected static ScheduledExecutorService timer = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);

	protected static ThreadPoolExecutor scheduler = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
	static {
		scheduler.setKeepAliveTime(10, TimeUnit.SECONDS);
		scheduler.allowCoreThreadTimeOut(true);
	}
	static WebBackgroundTaskScheduler instance;

	private WebBackgroundTaskScheduler(){

	}

	public static WebBackgroundTaskScheduler getInstance(){
		if(instance==null){
			instance=new WebBackgroundTaskScheduler();
		}
		return instance;
	}

	public void startScheduler() {
		prepareTask();
		calcTomorrow();
		timer.scheduleAtFixedRate(this,1000,timerPeriod,TimeUnit.MILLISECONDS);
	}

	public void stopScheduler(){
		for (AbstractDailyTask task : tasks) {
			task.setScheduleCancel(true);
			task.interruptTask();
		}
		scheduler.shutdownNow();
		timer.shutdownNow();
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		for (AbstractDailyTask task : tasks) {
			if (task.timeToFire(time)) {
				scheduler.execute(task);
			}
		}
		
		if (time >= tomorrow) {
			log.info("new day");
			calcTomorrow();
			for (AbstractDailyTask task : tasks) {
				new Thread(){
					@Override
					public void run() {
						task.newDayBegin();
					}
				}.start();
			}
		}
	}

	/**
	 * 计算明天零时00:00:00的unix时间戳。
	 */
	private void calcTomorrow() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		String time = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()) + " 00:00:00";
		try {
			tomorrow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime();
		} catch (ParseException e) {
		}
	}

	private void prepareTask() {
		tasks = new ArrayList<>();
		final Properties p = ResourceReader.readProperties("/schedule.properties");
		String pack=getClass().getPackage().getName();
		pack=pack.substring(0,pack.lastIndexOf('.'));
		new FastClasspathScanner(pack)
				.matchSubclassesOf(AbstractDailyTask.class, new SubclassMatchProcessor<AbstractDailyTask>() {

					@Override
					public void processMatch(Class<? extends AbstractDailyTask> matchingClass) {
						int mod = matchingClass.getModifiers();
						if (!Modifier.isAbstract(mod)) {
							try {
								AbstractDailyTask task = matchingClass.newInstance();

								String s = (String) p.get(task.getClass().getName());
								if (s != null) {
									task.schedules = Schedule.parse(s);
								}
								task.newDayBegin();
								tasks.add(task);
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
				}).scan();
	}

	public static AbstractDailyTask getTaskByName(String name) {
		for (AbstractDailyTask task : tasks) {
			if (task.getClass().getName().equals(name)) {
				return task;
			}
		}
		return null;
	}
}
