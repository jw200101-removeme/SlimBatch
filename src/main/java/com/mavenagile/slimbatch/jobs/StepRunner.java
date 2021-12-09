package com.mavenagile.slimbatch.jobs;

import java.util.ArrayList;

import com.mavenagile.slimbatch.Partition;
import com.mavenagile.slimbatch.PartitionInfo;
import com.mavenagile.slimbatch.confi.elements.Step;
import com.mavenagile.slimbatch.config.SlimBatch;

public class StepRunner
{
	private Step oStep;
	private JobRunner oParentJob;
	private ArrayList<RunnerBase> oRunninWorkers;
	private int oWorkersNumber;
	private boolean oDoneFiring;

	public StepRunner(JobRunner varParentJobRunner, Step varStep)
	{
		oParentJob = varParentJobRunner;
		oStep = varStep;
		oRunninWorkers = new ArrayList<RunnerBase>();
		oWorkersNumber = 0;
		oDoneFiring = false;
	}

	/**
	 * Return true if failed
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean runStep() throws Exception
	{
		System.out.println("	Run Step : " + oStep.getID());
		if (oStep.getChunk() != null)
		{
			if (oStep.getPartition() == null)
			{
				ChunkRunner aChunkRunner = new ChunkRunner(this, oStep, null);
				if (aChunkRunner.isFailed())
					return (true);
				oRunninWorkers.add(aChunkRunner);
				new Thread(aChunkRunner).start();
				oWorkersNumber++;
				oDoneFiring = true;
				return (false);
			}
			else
			{
				Partition aPartition = (Partition) SlimBatch.getInstance().getConfiguration().getBean(oStep.getPartition().getMapper().getRef(), oStep.getPartition().getMapper().getProperties());
				int i = 0;
				PartitionInfo tmpPartitionInfo = aPartition.getPartitions(i++);
				ChunkRunner tmpChunkRunner;
				while (tmpPartitionInfo != null)
				{
					oWorkersNumber++;
					tmpChunkRunner = new ChunkRunner(this, oStep, tmpPartitionInfo);

					if (tmpChunkRunner.isFailed())
						return (true);
					oRunninWorkers.add(tmpChunkRunner);
					new Thread(tmpChunkRunner).start();
					tmpPartitionInfo = aPartition.getPartitions(i++);

				}
				oDoneFiring = true;
//				System.out.println("SHoud start Thread : " + getClass().getName() + " : called : " + oWorkersNumber + " : " + oRunninWorkers.size());
				// int index = 0;
				// synchronized (oRunninWorkers)
				// {
				// for (ChunkRunner aChunkRunner : aChunkRunnerList)
				// {
				// index++;
				// oRunninWorkers.add(aChunkRunner);
				// new Thread(aChunkRunner).start();
				// // System.out.println("Started Thread : " + getClass().getName() + " : called : " + index);
				// }
				// }

				return (false);
			}
		}
		else if (oStep.getChunk() == null)
		{
			BatchletRunner aBatchletRunner = new BatchletRunner(this, oStep, null);
			if (aBatchletRunner.isFailed())
				return (true);
			oRunninWorkers.add(aBatchletRunner);
			new Thread(aBatchletRunner).start();
			oWorkersNumber++;
			oDoneFiring = true;
			return (false);
		}
		return (false);
	}

	public void stopRunner()
	{
		if (oRunninWorkers.isEmpty() == false)
		{
			for (RunnerBase aRunner : oRunninWorkers)
			{
				oWorkersNumber--;
				aRunner.stopWorker(true);
			}
		}
	}

	private RunnerBase getRunner(RunnerBase varRunnerBase)
	{
		for (RunnerBase aRunner : oRunninWorkers)
		{
			if (aRunner == varRunnerBase)
			{
				return (aRunner);
			}
		}
		return (null);
	}

	public synchronized void runCompleted(RunnerBase varRunnerBase)
	{
		// System.out.println("runCompleted called Parent : " + varRunnerBase.getClass().getName() + " : Count was : " + oWorkersNumber + " : " + oRunninWorkers.size());

		synchronized (oRunninWorkers)
		{
			// if (oRunninWorkers.isEmpty() == false)
			{
				RunnerBase aRunner = null;
				for (RunnerBase tmpRunner : oRunninWorkers)
				{
					if (tmpRunner == varRunnerBase)
					{
						aRunner = tmpRunner;
						break;
					}
				}
				// System.out.println(" runCompleted called Parent aRunner : " + aRunner + " : Count IS : " + oWorkersNumber);

				if (aRunner != null)
				{
					oRunninWorkers.remove(aRunner);
					oWorkersNumber--;
				}
			}
			// System.out.println("runCompleted oRunninWorkers : " + getClass().getName() + " : called : " + oRunninWorkers.size());

			// if (oRunninWorkers.isEmpty() && oWorkersNumber == 0)
			if (oWorkersNumber == 0 && oDoneFiring)
			{
				// System.out.println("runCompleted oRunninWorkers.isEmpty() : " + getClass().getName() + " : called ");
				oParentJob.stepCompleted();
			}
		}
		// System.out.println(" runCompleted called Parent : " + varRunnerBase.getClass().getName() + " : Count IS : " + oWorkersNumber);
	}
}
