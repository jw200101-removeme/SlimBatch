package demo.mavenagile.slimbatch.sillybatch.domain;

import java.sql.Connection;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.dbaccess.DBSession;

public class SillyDataHelper
{
	private Log LOGGER = LogFactory.getLog(SillyDataHelper.class);
	private static boolean oMockDataSet = false;
	private Random oRandom = new Random();

	public boolean dbCreated(Connection varConnection)
	{
		SillyDAO aDAO = new SillyDAO(varConnection);
		aDAO.selectSilly(-1);
		return (aDAO.getErrorCode() == -1403);
	}

	public void setupSilyMockData(DataSource varDataSource, int varNumberOfRecords) throws Exception
	{
		if (oMockDataSet == true)
			return;
		boolean aSessionStatus = false;
		DBSession aDBSession = new DBSession(varDataSource.getConnection());
		aSessionStatus = aDBSession.beginTransaction();
		if (aSessionStatus == false)
		{
			oMockDataSet = true;
			throw new Exception("Invalid DBSession status");
		}
		SillyDAO aDAO = new SillyDAO(aDBSession.getConnection());
		aDAO.deteletAllFromSilly();
		LOGGER.debug("SillyDataHelper::setupSilyMockData Deleted old silly data : " + aDAO.getErrorCode() + " : " + aDAO.getErrorMessage());
		aSessionStatus = aDBSession.commitTransaction();
		if (aSessionStatus == false)
		{
			oMockDataSet = true;
			throw new Exception("Invalid DBSession status");
		}
		aSessionStatus = aDBSession.beginTransaction();
		if (aSessionStatus == false)
		{
			oMockDataSet = true;
			throw new Exception("Invalid DBSession status");
		}
		if (varNumberOfRecords <= 0)
			varNumberOfRecords = 1000;
		LOGGER.debug("SillyDataHelper::setupSilyMockData Will insert new silly data : [" + varNumberOfRecords + "]");
		for (int i = 0; i < varNumberOfRecords; i++)
		{
			aDAO.insertSilly(new SillyDO(i, 0, 0, 0, 0, 0, null, null));
			if (aDAO.getErrorCode() != 0)
			{
				oMockDataSet = true;
				throw new Exception("Error while inserting data : " + aDAO.getErrorCode() + " : " + aDAO.getErrorMessage());
			}
			if (i % 100 == 0)
			{
				aSessionStatus = aDBSession.commitTransaction();
				if (aSessionStatus == false)
					throw new Exception("Invalid DBSession status");
				aSessionStatus = aDBSession.beginTransaction();
				if (aSessionStatus == false)
				{
					oMockDataSet = true;
					throw new Exception("Invalid DBSession status");
				}
			}
		}
		aSessionStatus = aDBSession.commitTransaction();
		if (aSessionStatus == false)
		{
			oMockDataSet = true;
			throw new Exception("Invalid DBSession status");
		}
		LOGGER.debug("SillyDataHelper::setupSilyMockData Commited new silly data [" + varNumberOfRecords + "] : Commit status : " + aDBSession.getErrorCode() + " : " + aDBSession.getErrorMessage());
		aSessionStatus = aDBSession.close();
		if (aSessionStatus == false)
		{
			oMockDataSet = true;
			throw new Exception("Invalid DBSession status");
		}
		oMockDataSet = true;
	}

	public boolean isMockDataSet()
	{
		return oMockDataSet;
	}
 

	public boolean processorSilly(SillyDO varDO)
	{
		if ((oRandom.nextInt(100) + 1) % 13 == 0)
			return (false);

		varDO.setNum(varDO.getID());
		varDO.setSquare(varDO.getNum() * varDO.getNum());
		varDO.setCube(varDO.getSquare() * varDO.getNum());
		varDO.setQuartic(varDO.getCube() * varDO.getNum());
		varDO.setQuintic(varDO.getQuartic() * varDO.getNum());
		varDO.setFlag("C");
		return (true);
	}

	public boolean processorSillyStep1(SillyDO varDO)
	{
		varDO.setFlag("C0");
		if ((oRandom.nextInt(100) + 1) % 13 == 0)
			return (false);
		varDO.setFlag("C1");
		varDO.setNum(varDO.getID());
		return (true);
	}

	public boolean processorSillyStep2(SillyDO varDO)
	{
		varDO.setFlag("C2");
		varDO.setSquare(varDO.getNum() * varDO.getNum());
		return (true);
	}

	public boolean processorSillyStep3(SillyDO varDO)
	{
		varDO.setFlag("C3");
		varDO.setCube(varDO.getSquare() * varDO.getNum());
		return (true);
	}

	public boolean processorSillyStep4(SillyDO varDO)
	{
		varDO.setFlag("C4");
		varDO.setQuartic(varDO.getCube() * varDO.getNum());
		return (true);
	}

	public boolean processorSillyStep5(SillyDO varDO)
	{
		varDO.setFlag("C5");
		varDO.setQuintic(varDO.getQuartic() * varDO.getNum());
		return (true);
	}

}
