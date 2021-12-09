package demo.mavenagile.slimbatch.sillybatch.batch;

import com.mavenagile.slimbatch.PartitionInfo;

public class SillyPartitionInfo implements PartitionInfo
{
	private int oID;
	private int oFrom;
	private int oTo;

	public int getID()
	{
		return oID;
	}

	public void setID(int varID)
	{
		oID = varID;
	}

	public int getFrom()
	{
		return oFrom;
	}

	public void setFrom(int varFrom)
	{
		oFrom = varFrom;
	}

	public int getTo()
	{
		return oTo;
	}

	public void setTo(int varTo)
	{
		oTo = varTo;
	}

	@Override
	public String toString()
	{
		return "SillyPartitionInfo [oID=" + oID + ", oFrom=" + oFrom + ", oTo=" + oTo + "]";
	}

}
