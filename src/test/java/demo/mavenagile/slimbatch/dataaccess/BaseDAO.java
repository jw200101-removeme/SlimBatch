package demo.mavenagile.slimbatch.dataaccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An abstract base class for data access objects
 * 
 *
 * @param <T>
 */
public abstract class BaseDAO<T>
{

	public static final int ERROR_CODE_NO_ERROR = 0;
	public static final int ERROR_CODE_DEFAULT = -1;
	public static final int ERROR_CODE_DEFAULT_EXCEPTION = -2;
	public static final int ERROR_CODE_DEFAULT_SQL_EXCEPTION = -3;

	public static final int ERROR_CODE_EXCEPTION_ILLEGALSTATE = -34;
	public static final int ERROR_CODE_EXCEPTION_INVALIDPARMS = -39;
	public static final int ERROR_CODE_EXCEPTION_NOSUCHMETHODEXCEPTION = -102;
	public static final int ERROR_CODE_EXCEPTION_INVOCATIONTARGET = -103;
	public static final int ERROR_CODE_EXCEPTION_ILLEGALACCESS = -104;
	public static final int ERROR_CODE_EXCEPTION_ILLEGALARGUMENT = -105;

	/**
	 * No records found
	 */
	public static final int ERROR_CODE_NOT_FOUND = -1403;
	/**
	 * result set is null
	 */
	public static final int ERROR_CODE_RESULTSET_IS_NULL = -1503;

	/**
	 * No records inserted
	 */
	public static final int ERROR_CODE_NOT_INSERTED = -1411;
	/**
	 * No records updated
	 */
	public static final int ERROR_CODE_NOT_UPDATED = -1412;
	/**
	 * No records deleted
	 */
	public static final int ERROR_CODE_NOT_DELTED = -1413;
	public static final int ERROR_CODE_DELTED_ERROR = -1513;

	protected Connection oConnection;
	protected boolean oStatus;
	protected int oErrorCode;
	protected String oErrorMessage;

	protected static Log LOGGER = LogFactory.getLog(BaseDAO.class);

	protected abstract T readATableRow(ResultSet varResultSet) throws SQLException;

	protected BaseDAO()
	{
	}

	protected BaseDAO(Connection varConnection)
	{
		oConnection = varConnection;
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

	/**
	 * Make sure error numbers are negative numbers
	 * 
	 * @param varErrorNum
	 * @return
	 */
	protected static Integer getErrorCode(int varErrorNum)
	{
		if (varErrorNum == ERROR_CODE_NO_ERROR)
			return (new Integer(ERROR_CODE_DEFAULT));
		if (varErrorNum > 0)
			return (new Integer(-varErrorNum));
		return (new Integer(varErrorNum));
	}

	protected static void setUpPrepareedStatement(PreparedStatement varPStatement, Object[] varPSObjects) throws SQLException
	{
		if (varPSObjects == null)
			return;

		for (int j = varPSObjects.length, i = varPSObjects.length - 1; i >= 0; i--, j--)
		{
			if (varPSObjects[i] == null)
				// DB2 doesn't like java.sql.Types.NULL
				varPStatement.setNull(j, java.sql.Types.VARCHAR);
			else if (varPSObjects[i] instanceof java.lang.String)
				varPStatement.setString(j, (String) varPSObjects[i]);
			else if (varPSObjects[i] instanceof java.lang.Integer)
				varPStatement.setInt(j, ((Integer) varPSObjects[i]).intValue());
			else if (varPSObjects[i] instanceof java.math.BigDecimal)
				varPStatement.setBigDecimal(j, ((java.math.BigDecimal) varPSObjects[i]));
			else if (varPSObjects[i] instanceof java.sql.Blob)
				varPStatement.setBlob(j, ((Blob) varPSObjects[i]));
			else if (varPSObjects[i] instanceof java.lang.Boolean)
				varPStatement.setBoolean(j, ((Boolean) varPSObjects[i]).booleanValue());
			else if (varPSObjects[i] instanceof java.lang.Byte)
				varPStatement.setByte(j, ((Byte) varPSObjects[i]).byteValue());
			else if (varPSObjects[i] instanceof java.sql.Clob)
				varPStatement.setClob(j, ((Clob) varPSObjects[i]));
			else if (varPSObjects[i] instanceof java.lang.Double)
				varPStatement.setDouble(j, ((Double) varPSObjects[i]).doubleValue());
			else if (varPSObjects[i] instanceof java.lang.Long)
				varPStatement.setLong(j, ((Long) varPSObjects[i]).longValue());
			else if (varPSObjects[i] instanceof java.lang.Float)
				varPStatement.setFloat(j, ((Float) varPSObjects[i]).floatValue());
			else if (varPSObjects[i] instanceof java.sql.Ref)
				varPStatement.setRef(j, ((Ref) varPSObjects[i]));
			else if (varPSObjects[i] instanceof java.lang.Short)
				varPStatement.setShort(j, ((Short) varPSObjects[i]).shortValue());
			else if (varPSObjects[i] instanceof java.sql.Time)
				varPStatement.setTime(j, (Time) varPSObjects[i]);
			else if (varPSObjects[i] instanceof java.sql.Timestamp)
				varPStatement.setTimestamp(j, (Timestamp) varPSObjects[i]);
			else if (varPSObjects[i] instanceof java.util.Date)
				varPStatement.setDate(j, ((Date) varPSObjects[i]));
			else if (varPSObjects[i] instanceof java.sql.Array)
				varPStatement.setArray(j, (java.sql.Array) varPSObjects[i]);
			else
				varPStatement.setObject(j, varPSObjects[i]);
		}
	}

	protected int insert(String varQuery, Object[] varPSObjects)
	{
		oStatus = false;
		oErrorCode = ERROR_CODE_NO_ERROR;
		oErrorMessage = null;
		PreparedStatement theStatement = null;
		try
		{
			theStatement = oConnection.prepareStatement(varQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			theStatement.setEscapeProcessing(true);
			if (varPSObjects != null)
				setUpPrepareedStatement(theStatement, varPSObjects);

			int aResultCode = theStatement.executeUpdate();
			if (aResultCode <= ERROR_CODE_NO_ERROR)
			{
				oErrorCode = BaseDAO.ERROR_CODE_NOT_INSERTED;
				oErrorMessage = "No records inserted";
			}
			theStatement.close();
			oStatus = true;
			return (aResultCode);
		}
		catch (SQLException ex)
		{
			try
			{
				if (theStatement != null && theStatement.isClosed() == false)
					theStatement.close();
			}
			catch (SQLException e)
			{
				// eat up exception, nothing we can do
				LOGGER.error("Second attempt to close statment failed : ", ExceptionHelper.getRootCause(e));
			}
			oStatus = false;
			oErrorCode = getErrorCode(ex.getErrorCode());
			if (oErrorCode == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_DEFAULT_SQL_EXCEPTION;
			oErrorMessage = ex.getMessage();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ex));
		}
		return (ERROR_CODE_DEFAULT_EXCEPTION);

	}

	protected int update(String varQuery, Object[] varPSObjects)
	{
		oStatus = false;
		oErrorCode = ERROR_CODE_NO_ERROR;
		oErrorMessage = null;
		PreparedStatement theStatement = null;
		try
		{
			theStatement = oConnection.prepareStatement(varQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			theStatement.setEscapeProcessing(true);
			if (varPSObjects != null)
				setUpPrepareedStatement(theStatement, varPSObjects);
		
			int aResultCode = theStatement.executeUpdate();
			if (aResultCode <= ERROR_CODE_NO_ERROR)
			{
				oErrorCode = BaseDAO.ERROR_CODE_NOT_UPDATED;
				oErrorMessage = "No records updated";
			}
			theStatement.close();
			oStatus = true;
			return (aResultCode);
		}
		catch (SQLException ex)
		{
			try
			{			
				if (theStatement.isClosed() == false)
					theStatement.close();
			}
			catch (SQLException e)
			{
				// eat up exception, nothing we can do
				LOGGER.error("Second attempt to close statment failed : ", ExceptionHelper.getRootCause(e));
			}
			oStatus = false;
			oErrorCode = getErrorCode(ex.getErrorCode());
			if (oErrorCode == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_DEFAULT_SQL_EXCEPTION;
			oErrorMessage = ex.getMessage();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ex));
		}
		return (ERROR_CODE_DEFAULT_EXCEPTION);
	}

	protected int delete(String varQuery, Object[] varPSObjects)
	{
		oStatus = false;
		oErrorCode = ERROR_CODE_NO_ERROR;
		oErrorMessage = null;
		PreparedStatement theStatement = null;
		try
		{
			theStatement = oConnection.prepareStatement(varQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			theStatement.setEscapeProcessing(true);
			if (varPSObjects != null)
				setUpPrepareedStatement(theStatement, varPSObjects);
			oErrorCode = theStatement.executeUpdate();
			oStatus = true;
			if (oErrorCode == BaseDAO.ERROR_CODE_NO_ERROR)
			{
				oErrorCode = BaseDAO.ERROR_CODE_NOT_DELTED;
				oErrorMessage = "No records deleted";
			}
			else if (oErrorCode < 0)
			{
				oErrorCode = ERROR_CODE_DELTED_ERROR;
				oErrorMessage = "An error occurred while deleting records";
				oStatus = false;
			}
			theStatement.close();
			return (oErrorCode);
		}
		catch (SQLException ex)
		{
			try
			{
				if (theStatement != null && theStatement.isClosed() == false)
					theStatement.close();
			}
			catch (SQLException e)
			{
				// eat up exception, nothing we can do
				LOGGER.error("Second attempt to close statment failed : ", ExceptionHelper.getRootCause(e));
			}
			oStatus = false;
			oErrorCode = getErrorCode(ex.getErrorCode());
			if (oErrorCode == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_DEFAULT_SQL_EXCEPTION;
			oErrorMessage = ex.getMessage();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ex));
		}
		return (ERROR_CODE_DEFAULT_EXCEPTION);
	}

	/**
	 * If query doesn't return result, oStatus is set to true and oErrorCode to -1403
	 * 
	 * @param varQuery
	 * @param varPSObjects
	 * @param varCallBackMethod
	 * @return
	 */
	protected T select(String varQuery, Object varPSObjects[], String varCallBackMethod)
	{
		oStatus = false;
		oErrorCode = ERROR_CODE_NO_ERROR;
		oErrorMessage = null;
		PreparedStatement theStatement = null;
		T tmpDO = null;
		ResultSet theResultSet = null;
		try
		{
			theStatement = oConnection.prepareStatement(varQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			theStatement.setEscapeProcessing(true);
			if (varPSObjects != null)
				setUpPrepareedStatement(theStatement, varPSObjects);
			theResultSet = theStatement.executeQuery();

			if (theResultSet != null && theResultSet.next())
				tmpDO = invokereadResultRow(theResultSet, varCallBackMethod);
			else
			{
				oErrorCode = BaseDAO.ERROR_CODE_NOT_FOUND;
				oErrorMessage = "No records found";
				oStatus = true;
			}
			if (theResultSet != null)
				theResultSet.close();
			theStatement.close();
			return (tmpDO);
		}
		catch (SQLException ex)
		{
			try
			{
				if (theResultSet != null && theResultSet.isClosed() == false)
					theResultSet.close();

				if (theStatement.isClosed() == false)
					theStatement.close();
			}
			catch (SQLException e)
			{
				// eat up exception, nothing we can do
				LOGGER.error("Second attempt to close statment failed : ", ExceptionHelper.getRootCause(e));
			}
			oStatus = false;
			oErrorCode = getErrorCode(ex.getErrorCode());
			if (oErrorCode == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_DEFAULT_SQL_EXCEPTION;
			oErrorMessage = ex.getMessage();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ex));
		}
		return (null);
	}

	protected Object selectObject(String varQuery, Object varPSObjects[], String varCallBackMethod)
	{
		return (select(varQuery, varPSObjects, varCallBackMethod));
	}

	protected List<T> selectList(String varQuery, Object varPSObjects[], String varCallBackMethod)
	{
		PreparedStatement theStatement = null;
		ResultSet theResultSet = null;
		try
		{
			theStatement = oConnection.prepareStatement(varQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			theStatement.setEscapeProcessing(true);
			if (varPSObjects != null)
				setUpPrepareedStatement(theStatement, varPSObjects);

			List<T> theList = null;
			boolean aResultCode = theStatement.execute();
			if (aResultCode == false)
			{
				oErrorCode = BaseDAO.ERROR_CODE_NOT_FOUND;
				oErrorMessage = "Result set is empty, no records found";
				oStatus = true;
			}
			else
			{
				theResultSet = theStatement.getResultSet();
				theList = readResultList(theResultSet, varCallBackMethod);
				if (theList == null && oStatus == false)
					return (null);
				int tmpResultLength = theList.size();
				if (oStatus == true && tmpResultLength == ERROR_CODE_NO_ERROR)
				{
					oErrorCode = BaseDAO.ERROR_CODE_NOT_FOUND;
					oErrorMessage = "No records found";
					oStatus = true;
				}
				if (theResultSet != null)
					theResultSet.close();
			}
			theStatement.close();
			return (theList);
		}
		catch (SQLException ex)
		{
			try
			{
				if (theResultSet != null && theResultSet.isClosed() == false)
					theResultSet.close();

				if (theStatement.isClosed() == false)
					theStatement.close();
			}
			catch (SQLException e)
			{
				// eat up exception, nothing we can do
				LOGGER.error("Second attempt to close statment failed : ", ExceptionHelper.getRootCause(e));
			}
			oStatus = false;
			oErrorCode = getErrorCode(ex.getErrorCode());
			if (oErrorCode == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_DEFAULT_SQL_EXCEPTION;
			oErrorMessage = ex.getMessage();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ex));
		}
		return (null);
	}

	@SuppressWarnings("unchecked")
	private List<T> readResultList(ResultSet varResultSet, String varCallBackMethod) throws SQLException
	{
		if (varResultSet == null)
		{
			oStatus = false;
			oErrorCode = BaseDAO.ERROR_CODE_RESULTSET_IS_NULL;
			oErrorMessage = "Error: result set is null";
			LOGGER.error(oErrorMessage);
			return (null);
		}
		oStatus = true;
		oErrorCode = ERROR_CODE_NO_ERROR;
		oErrorMessage = null;
		List<T> aList = new ArrayList<T>();
		// Speed things up, don't call invokereadResultRow
		Method theMethod;
		Class<?> thisClass = getClass();
		Object[] aParameters = { varResultSet };

		Class<?> argTypes[] = { ResultSet.class };
		theMethod = getMethod(thisClass, varCallBackMethod, argTypes);
		if (theMethod == null)
			return (null);

		while (varResultSet.next())
		{
			try
			{
				aList.add((T) theMethod.invoke(this, aParameters));
			}
			catch (IllegalAccessException iae)
			{
				oStatus = false;
				oErrorCode = ERROR_CODE_EXCEPTION_ILLEGALACCESS;
				oErrorMessage = getClass().getSimpleName() + "::invokereadResultRow:IllegalAccessException : illegal access exception :  Metthod = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
				LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(iae));
				return (null);
			}
			catch (IllegalArgumentException iae)
			{
				oStatus = false;
				oErrorCode = ERROR_CODE_EXCEPTION_ILLEGALARGUMENT;
				oErrorMessage = getClass().getSimpleName() + "::invokereadResultRow:IllegalArgumentException : illegal argument :  Method = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
				LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(iae));
				return (null);
			}
			catch (InvocationTargetException ive)
			{
				oStatus = false;
				oErrorCode = ERROR_CODE_EXCEPTION_INVOCATIONTARGET;
				oErrorMessage = getClass().getSimpleName() + "::invokereadResultRow:InvocationTargetException : invocation target exception : Method = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
				LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ive));
				return (null);
			}
		}
		return (aList);
	}

	private Method getMethod(Class<?> varTheClass, String varMethodName, Class<?> varArgumentTypes[])
	{
		Class<?> tmpRootClass = varTheClass;
		Method theMethod = null;
		while (tmpRootClass != null)
		{
			try
			{
				theMethod = tmpRootClass.getDeclaredMethod(varMethodName, varArgumentTypes);
				return (theMethod);
			}
			// catch (NoSuchMethodException | SecurityException e)
			catch (Exception e)
			{
				// eat up exception on purpose
				// LOGGER.error("can not get methods for " + tmpRootClass + " : ", ExceptionHelper.getRootCause(e));
			}
			// give up if we reach as far as BaseDAO
			if (tmpRootClass == BaseDAO.class)
				break;
			tmpRootClass = tmpRootClass.getSuperclass();
		}
		oStatus = false;
		oErrorCode = ERROR_CODE_EXCEPTION_NOSUCHMETHODEXCEPTION;
		oErrorMessage = getClass().getSimpleName() + "::getMethod: no such Method [" + varMethodName + "]  in class  " + varTheClass.getName() + " or its super class";
		return (null);
	}

	@SuppressWarnings("unchecked")
	private T invokereadResultRow(ResultSet varResultSet, String varCallBackMethod)
	{
		Method theMethod;
		Object anInvokeResult;
		Class<?> thisClass = getClass();
		try
		{
			Class<?> theArgumentTypes[] = { ResultSet.class };
			theMethod = getMethod(thisClass, varCallBackMethod, theArgumentTypes);
			if (theMethod == null)
				return (null);
			Object[] aParameters = { varResultSet };
			anInvokeResult = theMethod.invoke(this, aParameters);
			return ((T) anInvokeResult);
		}
		catch (InvocationTargetException ive)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_EXCEPTION_INVOCATIONTARGET;
			oErrorMessage = "BaseDAO::invokereadResultRow:InvocationTargetException : invocation target exception : Method = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(ive));
			return (null);
		}
		catch (IllegalAccessException iae)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_EXCEPTION_ILLEGALACCESS;
			oErrorMessage = "BaseDAO::invokereadResultRow:IllegalAccessException : illegal access exception :  Metthod = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(iae));
			return (null);
		}
		catch (IllegalArgumentException iae)
		{
			oStatus = false;
			oErrorCode = ERROR_CODE_EXCEPTION_ILLEGALARGUMENT;
			oErrorMessage = "BaseDAO::invokereadResultRow:IllegalArgumentException : illegal argument :  Method = [" + varCallBackMethod + "] while processingquery in class : " + thisClass.getName();
			LOGGER.error(oErrorMessage, ExceptionHelper.getRootCause(iae));
			return (null);
		}
	}

	/**
	 * Read this method a single Integer value from ResultSet
	 * 
	 * @param varResultSet
	 * @return
	 * @throws SQLException
	 */
	protected Integer readAnInteger(ResultSet varResultSet) throws SQLException
	{
		Integer anIntResult = null;
		if (varResultSet != null)
		{
			anIntResult = varResultSet.getInt(1);
			oStatus = true;
			if (anIntResult == ERROR_CODE_NO_ERROR)
				oErrorCode = BaseDAO.ERROR_CODE_NOT_FOUND;
			else
				oErrorCode = ERROR_CODE_NO_ERROR;
		}
		else
		{
			oStatus = false;
			oErrorCode = BaseDAO.ERROR_CODE_RESULTSET_IS_NULL;
			oErrorMessage = "Error: invalid result set";
			LOGGER.error(oErrorMessage);
		}
		return (anIntResult);
	}

	/**
	 * Read this method a single Short value from ResultSet
	 * 
	 * 
	 * @param varResultSet
	 * @return
	 * @throws SQLException
	 */
	// TODO: Disabled, code not used yet
	// protected Short readAShort(ResultSet varResultSet) throws SQLException
	// {
	// Short aShortResult = null;
	// if (varResultSet != null)
	// {
	// aShortResult = varResultSet.getShort(1);
	// oStatus = true;
	// oErrorCode = BaseDAO.ERROR_CODE_NO_ERROR;
	// }
	// else
	// {
	// oStatus = false;
	// oErrorCode = BaseDAO.ERROR_CODE_RESULTSET_IS_NULL;
	// oErrorMessage = "Error: invalid result set";
	// LOGGER.error(oErrorMessage);
	// }
	// return (aShortResult);
	// }

	/**
	 * Read this method a single String value from ResultSet
	 * 
	 * @param varResultSet
	 * @return
	 * @throws SQLException
	 */
	// TODO: Disabled, code not used yet
	// protected String readAString(ResultSet varResultSet) throws SQLException
	// {
	// String aStringResult = null;
	// if (varResultSet != null)
	// {
	// aStringResult = varResultSet.getString(1);
	// oStatus = true;
	// oErrorCode = ERROR_CODE_NO_ERROR;
	// }
	// else
	// {
	// oStatus = false;
	// oErrorCode = BaseDAO.ERROR_CODE_RESULTSET_IS_NULL;
	// oErrorMessage = "Error: invalid result set";
	// LOGGER.error(oErrorMessage);
	// }
	// return (aStringResult);
	// }

	/**
	 * TODO: Remove me
	 */
	public void printStatus(String varTitle)
	{
		System.out.println(varTitle + " : Status : " + oStatus + " Error code : " + oErrorCode + " : " + oErrorMessage);
	}
}
