package com.mavenagile.slimbatch.jobs;

import java.util.ArrayList;
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
	protected ArrayList<Step> oStepsToRun;
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
		System.out.println("Start : " + oStarted);
		if (oStarted == false)
		{
			oStarted = true;
			return (runAStep());
		}
		return (null);
	}

	private Status runAStep() throws Exception
	{
		// for (Step aStep : oJob.getSteps())
		// {
		// oCurrentStep = new StepRunner(this, aStep);
		// if (oCurrentStep.runStep() == true)
		// break;
		// }
		// return (null);

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

			// System.out.println("Returned from runStep call, running in backgroud");
			// if (oStepsToRun.isEmpty())
			// jobCompleted("Second");
		}
		return (null);
	}

	private void jobCompleted(String varMessage)
	{
		System.out.println("JobRunner : ALL all done, all job steps are complete : " + varMessage);
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
	 * TODO: THis need to be private
	 */
	void stepCompleted()
	{
		// System.out.println("Step completed called");
		try
		{
			runAStep();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
