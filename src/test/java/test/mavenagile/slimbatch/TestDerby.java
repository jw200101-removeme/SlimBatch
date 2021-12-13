package test.mavenagile.slimbatch;

import java.sql.Connection;
import java.util.List;

import org.apache.derby.jdbc.EmbeddedXADataSource;

import com.mavenagile.slimbatch.config.SlimBatch;
import com.mavenagile.slimbatch.jobs.JobRunner;

import demo.mavenagile.slimbatch.sillybatch.SetupBD;
import demo.mavenagile.slimbatch.sillybatch.batch.SillyJobListener;
import demo.mavenagile.slimbatch.sillybatch.batch.SillyPartition;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDAO;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDO;

/**
 * Hello world!
 *
 */
public class TestDerby
{
	public static void verifyRecordCount() throws Exception
	{
		EmbeddedXADataSource aDataSource = new EmbeddedXADataSource();
		aDataSource.setDataSourceName("EmbeddedXADataSource");
		aDataSource.setUser("SillyDB");
		aDataSource.setPassword("SillyDB");
		aDataSource.setDatabaseName("/tmp/DerbyDB/SillyDB");

		Connection aConnection = aDataSource.getConnection();
		SillyDAO aDAO = new SillyDAO(aConnection);
		List<SillyDO> aList = aDAO.selectQuery("SELECT * FROM Silly where CCube=0");
		System.out.println("aList before : " + aList.size());
		SetupBD aSetupBD = new SetupBD(1000000);
		aSetupBD.createDerbyDB(false);
		aList = aDAO.selectQuery("SELECT * FROM Silly where CCube=0");
		System.out.println("aList after : " + aList.size());
	}

	public static void main(String[] args) throws Exception
	{
		int records = 1000000;
		SetupBD aSetupBD = new SetupBD(records);
		aSetupBD.createDerbyDB(false);
		SlimBatch aSlimBatch = SlimBatch.getInstance();

		JobRunner aJobRunner;
		aJobRunner = aSlimBatch.getJobRunner("SillyJob-UnPartitionedDerby", new SillyJobListener());
		aJobRunner.start();

		// reset DB
		aSetupBD.createDerbyDB(false);

		SillyPartition aSillyPartition = (SillyPartition) aSlimBatch.getConfiguration().getBean("SillyPartitionMapper", null);
		aSillyPartition.setNumberOfPartitions(50);
		aSillyPartition.setNumberOfRows(records);
		aJobRunner = aSlimBatch.getJobRunner("SillyJob-DynamicPartitionsDerby", new SillyJobListener());
		aJobRunner.start();

	}
}
