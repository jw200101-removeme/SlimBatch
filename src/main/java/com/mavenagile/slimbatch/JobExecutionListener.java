package com.mavenagile.slimbatch;

public interface JobExecutionListener
{
	void beforeJob();

	void afterJob();
	void jobFailed();
}
