package demo.mavenagile.slimbatch.sillybatch.batch;

import com.mavenagile.slimbatch.Partition;
import com.mavenagile.slimbatch.PartitionInfo;

public class SillyPartition implements Partition
{
	private int oNumberOfRows = 50000;
	private int oNumberOfPartitions = 20;

	public SillyPartition()
	{
		System.out.println("SillyPartition::SillyPartition");
	}

	public void setNumberOfPartitions(int varNumberOfPartitions)
	{
		oNumberOfPartitions = varNumberOfPartitions;
	}

	public int getNumberOfPartitions()
	{
		return oNumberOfPartitions;
	}

	public PartitionInfo getPartitions(int varIndex)
	{
		int chunk = oNumberOfRows / oNumberOfPartitions;
		if (varIndex < oNumberOfPartitions)
		{
			SillyPartitionInfo aSillyPartitionInfo = new SillyPartitionInfo();
			aSillyPartitionInfo.setID(varIndex);
			aSillyPartitionInfo.setFrom(varIndex * chunk + 1);
			if (varIndex == oNumberOfPartitions - 1)
				aSillyPartitionInfo.setTo(oNumberOfRows);
			else
				aSillyPartitionInfo.setTo((varIndex + 1) * chunk);
//			System.out.println("returning partion #" + varIndex + " : " + aSillyPartitionInfo + " : " + chunk + " : " + oNumberOfRows + " : " + oNumberOfPartitions);
			return (aSillyPartitionInfo);
		}
		else
			return (null);
	}

	public void setNumberOfRows(int varNumberOfRows)
	{
		oNumberOfRows = varNumberOfRows;
	}

	public void setPartitions(int varPartitions)
	{
		oNumberOfPartitions = varPartitions;
	}

	public int getNumberOfRows()
	{
		return oNumberOfRows;
	}

}
