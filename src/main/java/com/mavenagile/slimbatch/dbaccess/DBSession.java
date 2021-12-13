package com.mavenagile.slimbatch.dbaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper class around java.sql.Connection to manage save points.
 *
 *
 */
public class DBSession
{

	private static final String ERR_TEXT_NEW_TRANSACTION = "An error occurred while starting new transaction";
	private static final String ERR_TEXT_NO_TRANSACTION = "Error : No Transaction started";
	private static final String ERR_TEXT_STATE = "] : State [";
	private static final String ERR_TEXT_MESSAGE = "] Message [ ";

	public static final int ERROR_NO_ERROR = 0;
	public static final int ERROR_CODE_DEFAULT = -1;
	public static final int ERROR_CODE_CAN_NOT_COMMIT_CONNECTION = -11;
	public static final int ERROR_CODE_CAN_NOT_CLOSE_CONNECTION = -12;
	public static final int ERROR_CODE_CAN_NOT_ROLLBACK_CONNECTION = -13;

	public static final int ERROR_CODE_CAN_NOT_BEGIN_TRANSACTION = -23;
	public static final int ERROR_CODE_COMMITTRANSACTION_FAILED = -24;
	public static final int ERROR_CODE_CAN_NOT_COMMITTRANSACTION = -25;
	public static final int ERROR_CODE_CAN_NOT_ABANDONTRANSACTION = -26;
	public static final String SAVE_POINT_NAME = "UniqueSavepoint";

	protected Connection oConnection;
	protected int oSavePointNumber;
	protected List<Savepoint> oSavePoints;

	protected boolean oStatus;
	protected int oErrorCode;
	protected String oErrorMessage;
	private Log oLOGGER = LogFactory.getLog(DBSession.class);

	public DBSession(Connection varConnection)
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::DBSession()");
		oConnection = varConnection;
		if (oConnection != null)
		{
			oStatus = true;
			oErrorCode = ERROR_NO_ERROR;
		}
		else
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_DEFAULT;
		}
	}

	public Connection getConnection()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::getConnection()");
		return oConnection;
	}

	public boolean isStatus()
	{
		return oStatus;
	}

	public int getErrorCode()
	{
		return oErrorCode;
	}

	public String getErrorMessage()
	{
		return oErrorMessage;
	}

	public boolean beginTransaction()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::beginTransaction()");

		if (oSavePoints == null)
			oSavePoints = Collections.synchronizedList(new ArrayList<Savepoint>());
		try
		{
			oConnection.setAutoCommit(false);
			oSavePoints.add(oConnection.setSavepoint(SAVE_POINT_NAME + oSavePointNumber));
			oSavePointNumber++;
			oErrorMessage = null;
			oStatus = true;
			oErrorCode = ERROR_NO_ERROR;
		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_BEGIN_TRANSACTION;
			oErrorMessage = ERR_TEXT_NEW_TRANSACTION + " : beginTransaction[" + //
					e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + //
					DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (oStatus);
	}

	public boolean commitTransaction()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::commitTransaction()");
		if (oSavePoints == null || oSavePoints.isEmpty())
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_DEFAULT;
			oErrorMessage = DBSession.ERR_TEXT_NO_TRANSACTION;
			oLOGGER.error(oErrorMessage);
			return (oStatus);
		}
		try
		{
			// save point is invalidated at commit
			oConnection.commit();

			oErrorMessage = null;
			oSavePoints.clear();
			oStatus = true;
			return (oStatus);
		}
		catch (SQLException e1)
		{
			oLOGGER.warn("An error occurred while committing, will rollback : ", e1);
			oStatus = false;
			try
			{
				oConnection.rollback(oSavePoints.get(oSavePoints.size() - 1));
				oErrorCode = ERROR_CODE_COMMITTRANSACTION_FAILED;
				oErrorMessage = "An error occurred while committing a transaction : [" + e1.getErrorCode() + DBSession.ERR_TEXT_STATE + e1.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e1.getMessage() + ']';

			}
			catch (SQLException e)
			{
				oErrorCode = ERROR_CODE_CAN_NOT_COMMITTRANSACTION;
				oErrorMessage = "An error occurred while rolling back a failed commit transaction : [" //
						+ e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
				oLOGGER.error(oErrorMessage, e);
			}
		}
		return (oStatus);
	}

	public boolean rolbackTransaction()
	{

		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::rolbackTransaction()");
		if (oSavePoints == null || oSavePoints.isEmpty())
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_DEFAULT;
			oErrorMessage = DBSession.ERR_TEXT_NO_TRANSACTION;
			oLOGGER.error(oErrorMessage);
			return (oStatus);
		}
		try
		{
			oConnection.rollback(oSavePoints.get(oSavePoints.size() - 1));
			oConnection.releaseSavepoint(oSavePoints.remove(oSavePoints.size() - 1));
			oErrorMessage = null;
			oStatus = true;
			oErrorCode = ERROR_CODE_DEFAULT;
			return (oStatus);

		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_ABANDONTRANSACTION;
			oErrorMessage = ERR_TEXT_NEW_TRANSACTION + " : rolbackTransaction[" + e.getErrorCode() //
					+ DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE //
					+ e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (oStatus);
	}

	/**
	 * Will rollback and discard of save point
	 */
	public boolean abandonTransaction()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::abandonTransaction()");
		if (oSavePoints == null || oSavePoints.isEmpty())
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_DEFAULT;
			oErrorMessage = DBSession.ERR_TEXT_NO_TRANSACTION;
			oLOGGER.error(oErrorMessage);
			return (oStatus);
		}
		try
		{
			oConnection.rollback(oSavePoints.get(0));
			synchronized (oSavePoints)
			{
				Iterator<Savepoint> i = oSavePoints.iterator(); // Must be in synchronized block
				while (i.hasNext())
					oConnection.releaseSavepoint(i.next());
			}

			oSavePoints.clear();
			oErrorMessage = null;
			oStatus = true;
			oErrorCode = ERROR_CODE_DEFAULT;
			return (oStatus);

		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_ABANDONTRANSACTION;
			oErrorMessage = "An error occurred while starting new transaction : abandonTransaction[" + e.getErrorCode()//
					+ DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (oStatus);
	}

	public boolean commit()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::commit()");
		try
		{
			if (!oConnection.isClosed())
			{
				oConnection.commit();
				return (true);
			}
		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_COMMIT_CONNECTION;
			oErrorMessage = "An error occurred while committing connection : [" + e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (false);
	}

	public boolean rollback()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::rollback()");
		try
		{
			if (!oConnection.isClosed())
			{
				oConnection.rollback();
				return (true);
			}
		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_ROLLBACK_CONNECTION;
			oErrorMessage = "An error occurred while rolling back connection : [" + e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (false);
	}

	public boolean commitAndClose()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::commitAndClose()");
		try
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_DEFAULT;
			oErrorMessage = "Connection closed";
			if (!oConnection.isClosed())
			{
				oConnection.commit();
				oConnection.close();
				return (true);
			}
		}
		catch (SQLException e)
		{
			oErrorCode = ERROR_CODE_CAN_NOT_COMMIT_CONNECTION;
			oErrorMessage = "An error occurred while committing and closing connection : [" + e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (false);
	}

	public boolean close()
	{
		if (oLOGGER.isTraceEnabled())
			oLOGGER.trace("DBSession::close()");
		try
		{
			if (!oConnection.isClosed())
			{
				// DB drive bug, it throw The transaction remains active, and the connection cannot be closed. ERRORCODE=-4471,
				// SQLSTATE=null
				// it wouldn't close cleanly without setting autocommit=true
				oConnection.setAutoCommit(true);
				oConnection.close();
				oStatus = false;
				oErrorCode = ERROR_CODE_DEFAULT;
				oErrorMessage = "Connection closed";
				return (true);
			}
		}
		catch (SQLException e)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_CAN_NOT_CLOSE_CONNECTION;
			oErrorMessage = "An error occurred while closing connection : [" + e.getErrorCode() + DBSession.ERR_TEXT_STATE + e.getSQLState() + DBSession.ERR_TEXT_MESSAGE + e.getMessage() + ']';
			oLOGGER.error(oErrorMessage, e);
		}
		return (false);
	}
}
