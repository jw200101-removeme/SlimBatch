package com.mavenagile.slimbatch.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.PartitionInfo;
import com.mavenagile.slimbatch.confi.elements.Step;

public abstract class RunnerBase implements Runnable
{
	protected StepRunner oParentStepRunner;
	protected Step oStep;
	protected PartitionInfo oPartitionInfo;
	protected boolean oStopWorker = false;
	protected boolean oFailed = false;
	protected int oProcessed = 0;
	protected int oErrored = 0;

	protected static Log LOGGER = LogFactory.getLog(RunnerBase.class);

	private RunnerBase()
	{
		super();
	}

	public RunnerBase(StepRunner varParentStepRunner, Step varStep, PartitionInfo varPartitionInfo)
	{
		this();
		oParentStepRunner = varParentStepRunner;
		oStep = varStep;
		oPartitionInfo = varPartitionInfo;
		setup();
	}

	protected abstract void setup();

	protected abstract void preIterations();

	protected abstract boolean nextIteration() throws Exception;

	protected abstract void afterLastIteration();

	protected abstract void iterationFailed();

	protected void runCompleted()
	{
		oParentStepRunner.runCompleted(this);
	}

	@Override
	public void run()
	{
		
		if (oFailed == false)
		{
			preIterations();
			while (oStopWorker == false)
			{
				try
				{
					if (nextIteration() == false)
						break;
				}
				catch (Exception e)
				{
					oFailed = true;
					break;
				}
			}
			afterLastIteration();
		}
		runCompleted();
	}

	public boolean isFailed()
	{
		return oFailed;
	}

	protected abstract void forciblyStopped();

	public void stopWorker(boolean varStopWorker)
	{
		oStopWorker = varStopWorker;
		forciblyStopped();
	}

}
