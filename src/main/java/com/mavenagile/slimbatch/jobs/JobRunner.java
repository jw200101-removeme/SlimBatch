package com.mavenagile.slimbatch.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mavenagile.slimbatch.JobExecutionListener;
import com.mavenagile.slimbatch.confi.elements.Job;
import com.mavenagile.slimbatch.confi.elements.Step;
import com.mavenagile.slimbatch.config.SlimBatch;

public class JobRunner
{
	public enum Status
	{
		Starting, Started, Stopping, Stopped, Failed, Completed, Abandoned
	}

	protected JobExecutionListener oJobExecutionListener;
	protected SlimBatch oParent;
	protected Job oJob;
	protected String oJobName;
	protected int oJobID;
	protected Status oWorkerStatus;
	protected boolean oStopWorker = false;
	protected String oWorkerID;
	protected StepRunner oCurrentStep;
	protected List<Step> oStepsToRun;
	private boolean oStarted;

	public JobRunner(SlimBatch varSlimBatch, Job varJob, JobExecutionListener varJobExecutionListener)
	{
		oParent = varSlimBatch;
		oJob = varJob;
		oWorkerID = UUID.randomUUID().toString();
		oWorkerStatus = Status.Starting;
		oStepsToRun = new ArrayList<Step>();
		for (Step aStep : oJob.getSteps())
			oStepsToRun.add(aStep);
		oJobExecutionListener = varJobExecutionListener;
		if (oJobExecutionListener != null)
			oJobExecutionListener.beforeJob();
	}

	public Status start() throws Exception
	{
		if (oStarted == false)
		{
			oStarted = true;
			return (runAStep());
		}
		return (null);
	}

	private Status runAStep() throws Exception
	{
		if (oStepsToRun.isEmpty())
			jobCompleted("First");
		else
		{

			Step aStep = oStepsToRun.remove(0);
			oCurrentStep = new StepRunner(this, aStep);
			if (oCurrentStep.runStep() == true)
			{
				if (oJobExecutionListener != null)
					oJobExecutionListener.jobFailed();
				return (null);
			}

		}
		return (null);
	}

	private void jobCompleted(String varMessage)
	{
		oParent.jobCompleted(oJobName);
		if (oJobExecutionListener != null)
			oJobExecutionListener.afterJob();
	}

	public void stopWorker() throws Exception
	{
		oWorkerStatus = Status.Stopped;
		oStopWorker = true;
		if (oCurrentStep != null)
			oCurrentStep.stopRunner();
	}

	/**
	 * Note: THis need to be private
	 */
	void stepCompleted()
	{
		try
		{
			runAStep();
		}
		catch (Exception e)
		{
		}
	}

	public int getJobID()
	{
		return oJobID;
	}

	public void setID(String varJobName)
	{
		oJobName = varJobName;
	}

}
