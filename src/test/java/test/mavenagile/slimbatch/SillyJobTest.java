package test.mavenagile.slimbatch;

import com.mavenagile.slimbatch.JobExecutionListener;
import com.mavenagile.slimbatch.config.SlimBatch;
import com.mavenagile.slimbatch.jobs.JobRunner;

import demo.mavenagile.slimbatch.sillybatch.SetupBD;
import demo.mavenagile.slimbatch.sillybatch.batch.SillyJobListener;
import demo.mavenagile.slimbatch.sillybatch.batch.SillyPartition;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple TestUnPartitioned.
 */
public class SillyJobTest extends TestCase implements JobExecutionListener
{
	private boolean oSecondTestRun = false;
	private long oTime;

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public SillyJobTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(SillyJobTest.class);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(SillyJobTest.class);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testUnPartitioned() throws Exception
	{
		SetupBD aSetupBD = new SetupBD(100000);
		aSetupBD.createDerbyDB(true);
		SlimBatch aSlimBatch = SlimBatch.getInstance();
		assertNotNull(aSlimBatch);
		assertNotNull(aSlimBatch.getConfiguration().toXML());
		SillyPartition aSillyPartition = (SillyPartition) aSlimBatch.getConfiguration().getBean("SillyPartitionMapper", null);
		aSillyPartition.setNumberOfPartitions(25);
		aSillyPartition.setNumberOfRows(100000);

		JobRunner aJobRunner;
		aJobRunner = aSlimBatch.getJobRunner("SillyJob-UnPartitionedDerby", this);
		aJobRunner.start();

	}

	@Override
	public void beforeJob()
	{
		oTime = System.currentTimeMillis();
		System.out.println("Before job : " + oSecondTestRun + " Time : " + SillyJobListener.getTimeStamp(oTime));

	}

	@Override
	public void afterJob()
	{
		oTime = System.currentTimeMillis() - oTime;
		System.out.println("After job : " + oSecondTestRun + " Time : " + SillyJobListener.getTimeStamp(oTime));
		if (oSecondTestRun == false)
		{
			System.out.println("-------------------------------------------------");
			oSecondTestRun = true;
			SetupBD aSetupBD = new SetupBD(100000);
			try
			{
				aSetupBD.createDerbyDB(false);
				SlimBatch aSlimBatch = SlimBatch.getInstance();
				assertNotNull(aSlimBatch);
				assertNotNull(aSlimBatch.getConfiguration().toXML());
				SillyPartition aSillyPartition = (SillyPartition) aSlimBatch.getConfiguration().getBean("SillyPartitionMapper", null);
				aSillyPartition.setNumberOfPartitions(25);
				aSillyPartition.setNumberOfRows(100000);

				JobRunner aJobRunner;
				aJobRunner = aSlimBatch.getJobRunner("SillyJob-DynamicPartitionsDerby", this);
				aJobRunner.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				assertTrue("test failed", false);
			}

		}
	}

	@Override
	public void jobFailed()
	{
		assertTrue("test failed", false);
	}
}
