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
		System.out.println("Run Batchlet : ");
		System.out.println("	Step Partition : " + oStep.getPartition().getMapper().getRef());
		System.out.println("	Step Batchlet : " + "GetBatchlet()");
	}

	@Override
	protected void preIterations()
	{
		System.out.println("	I am running : firstIteration : " + getClass().getSimpleName());
	}

	@Override
	protected boolean nextIteration()
	{
		System.out.println("	I am running : nextIteration : " + getClass().getSimpleName());
		return (false);
	}

	@Override
	protected void iterationFailed()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void afterLastIteration()
	{
		System.out.println("	I am running : lastIteration : " + getClass().getSimpleName());
	}

	@Override
	protected void forciblyStopped()
	{
		System.out.println("	I am running : forciblyStopped : " + getClass().getSimpleName());
		afterLastIteration();
	}

}
