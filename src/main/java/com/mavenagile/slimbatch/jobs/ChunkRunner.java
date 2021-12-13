package com.mavenagile.slimbatch.jobs;

import javax.sql.DataSource;

import com.mavenagile.slimbatch.DBProcessor;
import com.mavenagile.slimbatch.DBReaderPartitioned;
import com.mavenagile.slimbatch.DBWriter;
import com.mavenagile.slimbatch.PartitionInfo;
import com.mavenagile.slimbatch.confi.elements.Step;
import com.mavenagile.slimbatch.confi.elements.Transaction;
import com.mavenagile.slimbatch.config.Configuration;
import com.mavenagile.slimbatch.config.SlimBatch;
import com.mavenagile.slimbatch.dbaccess.DBSession;

public class ChunkRunner extends RunnerBase
{

	protected DBSession aDBSession;
	protected Transaction oTransaction;
	protected DBReaderPartitioned oReader;;
	protected DBProcessor oProcessor;
	protected DBWriter oWriter;
	protected int oCommitIndex = 0;

	public ChunkRunner(StepRunner varParentStepRunner, Step varStep, PartitionInfo varPartitionInfo)
	{
		super(varParentStepRunner, varStep, varPartitionInfo);
	}

	@Override
	protected void setup()
	{
		SlimBatch aSlimBatch = SlimBatch.getInstance();
		Configuration aConfiguration = aSlimBatch.getConfiguration();
		oTransaction = oStep.getTransaction();
		DataSource aDataSource = null;
		try
		{
			aDataSource = aConfiguration.getDataSource(oTransaction.getDataSource());
			aDBSession = new DBSession(aDataSource.getConnection());

			oReader = (DBReaderPartitioned) aConfiguration.getBean(oStep.getChunk().getReader().getRef(), oStep.getChunk().getReader().getProperties());
			oReader.setConnection(aDBSession.getConnection());
			oReader.setPartitionInfo(oPartitionInfo);
			if (oReader.setupReader() == false)
				throw new Exception("Error while setting up Reader");

			oProcessor = (DBProcessor) aConfiguration.getBean(oStep.getChunk().getProcessor().getRef(), oStep.getChunk().getProcessor().getProperties());
			oProcessor.setConnection(aDBSession.getConnection());

			oWriter = (DBWriter) aConfiguration.getBean(oStep.getChunk().getWriter().getRef(), oStep.getChunk().getWriter().getProperties());
			oWriter.setConnection(aDBSession.getConnection());

		}
		catch (Exception e)
		{
			LOGGER.error("An error occurred while setting up step", e);
			oFailed = true;
		}
	}

	@Override
	protected void preIterations()
	{
	}

	@Override
	protected boolean nextIteration() throws Exception
	{
		Object aReadDO = oReader.readNextItem();
		if (aReadDO == null)
			return (false);

		oProcessed++;
		Object aProcessedDO = oProcessor.processItem(aReadDO);
		if (aProcessedDO != null)
		{
			if (oWriter.writeItem(aReadDO, aProcessedDO) == null)
			{
				aDBSession.rolbackTransaction();
				aDBSession.beginTransaction();
			}
			else
			{
				aDBSession.beginTransaction();
				if (oCommitIndex == oTransaction.getCommitSize())
				{
					aDBSession.commitTransaction();
					aDBSession.beginTransaction();
					oCommitIndex = 0;
				}
				else
					oCommitIndex++;
			}
		}
		else
			oErrored++;
		return (true);
	}

	@Override
	protected void iterationFailed()
	{
		aDBSession.rolbackTransaction();
		afterLastIteration();

	}

	@Override
	protected void afterLastIteration()
	{
		if (oCommitIndex > 0)
			aDBSession.commitTransaction();
		aDBSession.close();
	}

	@Override
	protected void forciblyStopped()
	{
		afterLastIteration();
	}
 

}
