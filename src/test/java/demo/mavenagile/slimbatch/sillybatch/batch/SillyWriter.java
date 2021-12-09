package demo.mavenagile.slimbatch.sillybatch.batch;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.DBWriter;

import demo.mavenagile.slimbatch.sillybatch.domain.SillyDAO;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDO;

public class SillyWriter implements DBWriter
{
	private Log LOGGER = LogFactory.getLog(SillyWriter.class);
	private Connection oConnection;
	private SillyDAO oDAO;

	public SillyWriter()
	{
		super();
	}

	public void setConnection(Connection varConnection)
	{
		oConnection = varConnection;
		oDAO = new SillyDAO(oConnection);
	}

	public Object writeItem(Object varReadDO, Object varProcessoredDO) throws Exception
	{
		oDAO.updateSilly((SillyDO) varProcessoredDO);
		if (oDAO.getErrorMessage() != null)
			return (null);

		LOGGER.trace("SillyWriter:writeItem saved to DB : " + varProcessoredDO);
		return (varProcessoredDO);
	}
}
