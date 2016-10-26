package com.efrontech.cronExample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * class to include for create schedule
 * @author jguyet
 *
 */
public abstract class ManageableTask implements Runnable
{
	private String pattern;
	
	private byte min = -1;
	private byte sec = -1;
	private byte hour = -1;
	private byte day_mount = -1;
	private byte mount = -1;
	private byte day_week = -1;
	private boolean hourPhour = false;
	private boolean minPmin = false;
	private boolean secPsec = false;
	private byte hh = -1;
	private byte mm = -1;
	private byte ss = -1;
	
	public abstract void process();
	
	//runnable
	public void run()
	{
		if (this.isgoodTime())
			this.process();
	}
	
	/**
	 * load pattern and start counter
	 * @param cronTime m/s h d(month) m d(week)
	 */
	public void launch(String cronTime)
	{
		this.setPattern(cronTime);
		if (this.parsePattern())
			this.start();
	}
	

	public static abstract class ManageTask extends ManageableTask
	{
		public ScheduledFuture<?>	task;

		public void cancel()
		{
			this.task.cancel(true);
			this.cancel();
		}
	}
	
	/**
	 * check pattern and return boolean its time for process
	 * @return
	 */
	public boolean isgoodTime()
	{
		
		if (hourPhour)
		{
			DateFormat df = new SimpleDateFormat("kk");
			String datecmp = df.format(new Date());
			
			byte h = Byte.parseByte(datecmp);
			byte tmp = hh;
			int i = 0;
			while (tmp != h)
			{
				if (tmp == 24)
					tmp = -1;
				tmp++;
				i++;
			}
			if (i == hour)
			{
				hh = h;
				return true;
			}
			return false;
		}
		else if (minPmin)
		{
			DateFormat df = new SimpleDateFormat("mm");
			String datecmp = df.format(new Date());
			
			byte h = Byte.parseByte(datecmp);
			byte tmp = mm;
			int i = 0;
			while (tmp != h)
			{
				if (tmp == 59)
					tmp = -1;
				tmp++;
				i++;
			}
			if (i == min)
			{
				mm = h;
				return true;
			}
			return false;
		}
		else if (secPsec)
		{
			DateFormat df = new SimpleDateFormat("ss");
			String datecmp = df.format(new Date());
			
			byte h = Byte.parseByte(datecmp);
			byte tmp = ss;
			int i = 0;
			while (tmp != h)
			{
				if (tmp == 59)
					tmp = -1;
				tmp++;
				i++;
			}
			if (i == sec)
			{
				ss = h;
				return true;
			}
			return false;
		}
		String form = "";
		String cmp = "";
		boolean first = true;
		if (min != -1)
		{
			form += "mm";
			cmp += min;
			first = false;
		}
		if (sec != -1)
		{
			form += (first?"":"-") + "ss";
			cmp += (first?"":"-") + sec;
			first = false;
		}
		if (hour != -1)
		{
			form += (first?"":"-") + "kk";
			cmp += (first?"":"-") + hour;
			first = false;
		}
		if (day_mount != -1)
		{
			form += (first?"":"-") + "dd";
			cmp += (first?"":"-") + day_mount;
			first = false;
		}
		if (mount != -1)
		{
			form += (first?"":"-") + "MM";
			cmp += (first?"":"-") + mount;
			first = false;
		}
		if (day_week != -1)
		{
			form += (first?"":"-") + "FF";
			cmp += (first?"":"-") + day_week;
			first = false;
		}
		DateFormat df = new SimpleDateFormat(form);
		
		String datecmp = df.format(new Date());
		
		System.out.println(datecmp + "|" + cmp);
		if (datecmp != null && cmp.equalsIgnoreCase(datecmp))
			return true;
		return false;
	}
	
	/**
	 * Start counter /secondes or minutes
	 */
	public void start()
	{
		if (sec != -1)
			GlobalTask.TaskSheduler.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
		else
			GlobalTask.TaskSheduler.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
	}
	
	/**
	 * return String CronTime pattern
	 * @return
	 */
	public String getParttern()
	{
		return (pattern);
	}
	
	/**
	 * replace pattern
	 * @param pattern
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}
	
	/**
	 * Parse pattern and update alls privates variables if pattern rejected return false 
	 * @return
	 */
	public boolean parsePattern()
	{
		String line = pattern.replace("*", "-1").trim();
		int size = line.length();
		// Splitting the line
		ArrayList<String> splitted = new ArrayList<String>();
		StringBuffer current = null;
		boolean quotes = false;
		for (int i = 0; i < size; i++) {
			char c = line.charAt(i);
			if (current == null) {
				if (c == '"') {
					current = new StringBuffer();
					quotes = true;
				} else if (c > ' ') {
					current = new StringBuffer();
					current.append(c);
					quotes = false;
				}
			} else {
				boolean closeCurrent;
				if (quotes) {
					closeCurrent = (c == '"');
				} else {
					closeCurrent = (c <= ' ');
				}
				if (closeCurrent) {
					if (current != null && current.length() > 0) {
						String str = current.toString();
						if (quotes) {
							str = escape(str);
						}
						splitted.add(str);
					}
					current = null;
				} else {
					current.append(c);
				}
			}
		}
		if (current != null && current.length() > 0) {
			String str = current.toString();
			if (quotes) {
				str = escape(str);
			}
			splitted.add(str);
			current = null;
		}
		if (splitted.size() != 5)
			return false;
		if (splitted.get(0).contains("/"))
		{
			if (!isnumeric(splitted.get(0).split("/")[0]))
				return errorformat("minute1");
			if (!isnumeric(splitted.get(0).split("/")[1]))
				return errorformat("seconde");
			min = Byte.parseByte(splitted.get(0).split("/")[0]);
			sec = Byte.parseByte(splitted.get(0).split("/")[1]);
		}
		else
		{
			if (!isnumeric(splitted.get(0)))
				return errorformat("minute2");
			min = Byte.parseByte(splitted.get(0));
		}
		if (!isnumeric(splitted.get(1)))
			return errorformat("hour");
		hour = Byte.parseByte(splitted.get(1));
		if (!isnumeric(splitted.get(2)))
			return errorformat("day of mount");
		day_mount = Byte.parseByte(splitted.get(2));
		if (!isnumeric(splitted.get(3)))
			return errorformat("mount");
		mount = Byte.parseByte(splitted.get(3));
		if (!isnumeric(splitted.get(4)))
			return errorformat("day week");
		day_week = Byte.parseByte(splitted.get(4));
		
		if (sec != -1 && sec > 59 || sec != -1 && sec < 1)
			return errorsize("seconde");
		if (min != -1 && min > 59 || min != -1 && min < 1)
			return errorsize("minute");
		if (hour != -1 && hour > 24 || hour != -1 && hour < 1)
			return errorsize("hour");
		if (day_mount != -1 && day_mount > 31 || day_mount != -1 && day_mount < 1)
			return errorsize("day of mount");
		if (mount != -1 && mount > 12 || mount != -1 && mount < 1)
			return errorsize("hour");
		if (day_week != -1 && day_week > 12 || day_week != -1 && day_week < 1)
			return errorsize("day week");
		if (min == -1 && sec == -1 && hour != -1 && day_mount == -1 && mount == -1 && day_week == -1)
		{
			hourPhour = true;
			DateFormat df = new SimpleDateFormat("kk");
			String datecmp = df.format(new Date());
			
			hh = Byte.parseByte(datecmp);
		}
		else if (min != -1 && sec == -1 && hour == -1 && day_mount == -1 && mount == -1 && day_week == -1)
		{
			minPmin = true;
			DateFormat df = new SimpleDateFormat("mm");
			String datecmp = df.format(new Date());
			
			mm = Byte.parseByte(datecmp);
		}
		else if (min == -1 && sec != -1 && hour == -1 && day_mount == -1 && mount == -1 && day_week == -1)
		{
			secPsec = true;
			DateFormat df = new SimpleDateFormat("ss");
			String datecmp = df.format(new Date());
			
			ss = Byte.parseByte(datecmp);
		}
		else if (min == -1)
			min = 1;
		return true;
	}
	
	private static boolean errorformat(String type)
	{
		System.out.println("Error format " + type);
		return false;
	}
	
	private static boolean errorsize(String type)
	{
		System.out.println("Error Number not comformed " + type);
		return false;
	}
	
	/**
	 * go next space
	 * @param str
	 * @return
	 */
	private static String escape(String str) {
		int size = str.length();
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < size; i++) {
			int skip = 0;
			char c = str.charAt(i);
			if (c == '\\') {
				if (i < size - 1) {
					char d = str.charAt(i + 1);
					if (d == '"') {
						b.append('"');
						skip = 2;
					} else if (d == '\\') {
						b.append('\\');
						skip = 2;
					} else if (d == '/') {
						b.append('/');
						skip = 2;
					} else if (d == 'b') {
						b.append('\b');
						skip = 2;
					} else if (d == 'f') {
						b.append('\f');
						skip = 2;
					} else if (d == 'n') {
						b.append('\n');
						skip = 2;
					} else if (d == 'r') {
						b.append('\r');
						skip = 2;
					} else if (d == 't') {
						b.append('\t');
						skip = 2;
					} else if (d == 'u') {
						if (i < size - 5) {
							String hex = str.substring(i + 2, i + 6);
							try {
								int code = Integer.parseInt(hex, 16);
								if (code >= 0) {
									b.append((char) code);
									skip = 6;
								}
							} catch (NumberFormatException e) {
								;
							}
						}
					}
				}
			}
			if (skip == 0) {
				b.append(c);
			} else {
				i += (skip - 1);
			}
		}
		return b.toString();
	}
	
	/**
	 * check if String is numerisable
	 * @param str
	 * @return
	 */
	public static boolean isnumeric(String str)
	{
		@SuppressWarnings("unused")
		float value;
		try {
			value = Float.parseFloat(str);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}