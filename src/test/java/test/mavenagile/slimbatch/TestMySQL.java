package test.mavenagile.slimbatch;

import com.mavenagile.slimbatch.config.SlimBatch;
import com.mavenagile.slimbatch.jobs.JobRunner;

import demo.mavenagile.slimbatch.sillybatch.batch.SillyJobListener;
import demo.mavenagile.slimbatch.sillybatch.batch.SillyPartition;

/**
 * Hello world!
 *
 */
public class TestMySQL
{
	public static void main(String[] args) throws Exception
	{
		int records = 1000000;
		// SetupBD aSetupBD = new SetupBD(records);
		// aSetupBD.createMySQLDB(false);
		SlimBatch aSlimBatch = SlimBatch.getInstance();
		SillyPartition aSillyPartition = (SillyPartition) aSlimBatch.getConfiguration().getBean("SillyPartitionMapper", null);
		aSillyPartition.setNumberOfPartitions(50);
		aSillyPartition.setNumberOfRows(records);

		JobRunner aJobRunner;
		// aJobRunner = aSlimBatch.getJobRunner("SillyJob-UnPartitionedMySQL", new SillyJobListener());
		// aJobRunner.start();
		// reset DB
		// aSetupBD.createMySQLDB(false);

		// aSillyPartition.setNumberOfPartitions(20);
		aJobRunner = aSlimBatch.getJobRunner("SillyJob-DynamicPartitionsMySQL", new SillyJobListener());
		aJobRunner.start();

	}
}
