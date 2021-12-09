package demo.mavenagile.slimbatch.sillybatch.batch;

import com.mavenagile.slimbatch.JobExecutionListener;

public class SillyJobListener implements JobExecutionListener
{
	private long oTime;

	@Override
	public void beforeJob()
	{
		oTime = System.currentTimeMillis();
		System.out.println("SillyJobListener::beforeJob " + "Job about to start, do some prep " );

	}

	@Override
	public void afterJob()
	{
		long now = System.currentTimeMillis();
		oTime = now - oTime;
		System.out.println("SillyJobListener::afterJob " + "Job finished, do some reporting, took "  + getTimeStamp(oTime));
	}

	@Override
	public void jobFailed()
	{
		oTime = System.currentTimeMillis() - oTime;
		System.out.println("SillyJobListener::jobFailed " + "ops Job failed, do something, after " + getTimeStamp(oTime));
	}

	public static String getTimeStamp(long varTime)
	{
		long millis = varTime - ((varTime / 1000)*1000);
		long second = (varTime / 1000) % 60;
		long minute = (varTime / (1000 * 60)) % 60;
		long hour = (varTime / (1000 * 60 * 60)) % 24;
		if (varTime < 1000)
			millis = varTime;
		return (String.format("%02d:%02d:%02d:%03d", hour, minute, second, millis));
	}
}
