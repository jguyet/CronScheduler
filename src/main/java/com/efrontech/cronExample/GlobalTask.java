package com.efrontech.cronExample;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class of launch all schedulers
 * @author jguyet
 *
 */
public class GlobalTask
{

	//1pool for one task
	public static ScheduledExecutorService	TaskSheduler	= Executors.newScheduledThreadPool(1);

	public static void loadGlobalScheduler()
	{
		//new task with cron syntax time
		new ExampleScheduled().launch("*/10 * * * *");
	}
}