package demo.mavenagile.slimbatch.sillybatch.batch;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.DBReaderPartitioned;
import com.mavenagile.slimbatch.PartitionInfo;

import demo.mavenagile.slimbatch.sillybatch.domain.SillyDAO;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDO;

public class SillyReader implements DBReaderPartitioned
{
	private Log LOGGER = LogFactory.getLog(SillyReader.class);
	private Connection oConnection;
	private SillyPartitionInfo oSillyPartitionInfo;
	private List<SillyDO> oSillyDOList;
	public static int oTotalReaders = 0;
	private int oReaderID;
	protected int oStart;
	protected int oEnd;
	private int oIndex = 0;

	public SillyReader()
	{
		super();

		oReaderID = ++oTotalReaders;
	}

	public void setConnection(Connection varConnection)
	{
		oConnection = varConnection;
	}

	public void setPartitionInfo(PartitionInfo varPartitionInfo)
	{
		oSillyPartitionInfo = SillyPartitionInfo.class.cast(varPartitionInfo);
	}

	public boolean setupReader()
	{
		// Fake, for non Partitioned run
		if (oSillyPartitionInfo == null)
		{
			oSillyPartitionInfo = new SillyPartitionInfo();
			oSillyPartitionInfo.setFrom(oStart);
			oSillyPartitionInfo.setTo(oEnd);
			// System.out.println("Fake partion : " + oStart + " : " + oEnd);
		}
		SillyDAO aDAO = new SillyDAO(oConnection);
		oSillyDOList = aDAO.selectAllWhereNumIsZero(oSillyPartitionInfo.getFrom(), oSillyPartitionInfo.getTo());
		if (aDAO.getErrorCode() != -1403 && aDAO.getErrorMessage() != null)
		{
			LOGGER.error("SillyBatchletReader::readSillyData [" + oReaderID + "] error while reading data : " + aDAO.getErrorCode() + " : " + aDAO.getErrorMessage());
			return (false);
		}
		return (true);
	}

	public Object readNextItem()
	{
		if (oIndex >= oSillyDOList.size())
		{
			if (LOGGER.isTraceEnabled())
				LOGGER.trace("SillyBatchletReader::readNextItem [" + oReaderID + "] reached end of cursor : [" + oIndex + ']');
			return (null);
		}
		return (oSillyDOList.get(oIndex++));
	}

	public boolean closeReader()
	{
		return false;
	}

	public void setStart(int varStart)
	{
		oStart = varStart;
	}

	public void setEnd(int varEnd)
	{
		oEnd = varEnd;
	}

}
