package com.mavenagile.slimbatch.jobs;

import com.mavenagile.slimbatch.PartitionInfo;
import com.mavenagile.slimbatch.confi.elements.Step;

public class BatchletRunner extends RunnerBase
{

	public BatchletRunner(StepRunner varParentStepRunner, Step varStep, PartitionInfo varPartitionInfo)
	{
		super(varParentStepRunner, varStep, varPartitionInfo);
	}

	@Override
	protected void setup()
	{
		// default method
	}

	@Override
	protected void preIterations()
	{
		// default method
		// running : firstIteration

	}

	@Override
	protected boolean nextIteration()
	{
		// default method
		// running : nextIteration :
		return (false);
	}

	@Override
	protected void iterationFailed()
	{
		// default method
	}

	@Override
	protected void afterLastIteration()
	{
		// default method
		// running : lastIteration
	}

	@Override
	protected void forciblyStopped()
	{
		// default method
		// running : forciblyStopped
		afterLastIteration();
	}

}
