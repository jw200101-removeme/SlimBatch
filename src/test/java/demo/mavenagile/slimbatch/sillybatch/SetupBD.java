package demo.mavenagile.slimbatch.sillybatch;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import demo.mavenagile.slimbatch.sillybatch.domain.SillyDAO;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDataHelper;

public class SetupBD
{
	public static final int SILLY_NUMBER_OF_RECORDS = 10000;
	public static final String SCHEMA_DERBY = "CREATE TABLE Silly (\n" //
			+ "  ID int NOT NULL,\n" //
			+ "  Num int DEFAULT NULL,\n" //
			+ "  Square bigint DEFAULT NULL,\n" //
			+ "  CCube bigint DEFAULT NULL,\n" //
			+ "  Quartic bigint DEFAULT NULL,\n" //
			+ "  Quintic bigint DEFAULT NULL,\n" //
			+ "  Flag varchar(2) DEFAULT NULL,\n" //
			+ "  LastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" //
			+ " CONSTRAINT Silly_UNIQUE_ID UNIQUE (ID) \n" //
			+ ")\n";
	public static final String SCHEMA_MySQL = "CREATE TABLE `Silly` (\n" //
			+ "  `ID` int NOT NULL,\n" //
			+ "  `Num` int DEFAULT NULL,\n" //
			+ "  `Square` bigint DEFAULT NULL,\n" //
			+ "  `CCube` bigint DEFAULT NULL,\n" //
			+ "  `Quartic` bigint DEFAULT NULL,\n" //
			+ "  `Quintic` bigint DEFAULT NULL,\n" //
			+ "  `Flag` varchar(2) DEFAULT NULL,\n" //
			+ "  `LastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" //
			+ "  PRIMARY KEY (`ID`),\n" //
			+ "  UNIQUE KEY `ID_UNIQUE` (`ID`) USING BTREE\n" //
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	protected int oNumberOfRecords = SILLY_NUMBER_OF_RECORDS;

	public SetupBD()
	{
	}

	public SetupBD(int varNumberOfRecords)
	{
		oNumberOfRecords = varNumberOfRecords;
	}

	private int loadSchema(Connection varConnection, String varQuery) throws SQLException
	{
		int rc;
		try
		{
			PreparedStatement psmt = varConnection.prepareStatement(varQuery);
			rc = psmt.executeUpdate();
			return (rc);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("'DROP TABLE' cannot be performed on") == false && e.getMessage().contains("Schema 'DerbyDB' does not exist") == false)
				throw e;
		}
		return (-1);

	}

	public void createDerbyDB(boolean varInsertNewData) throws Exception
	{
		boolean createDB = true;
		String dbAttributes = "create=false";
		File tmpHomeDir = new File("/tmp", "DerbyDB");
		tmpHomeDir.mkdirs();

		File tmpDir = new File(tmpHomeDir, "SillyDB");
		if (tmpDir.exists() && tmpDir.isDirectory())
			createDB = false;

		if (createDB)
			dbAttributes = "create=true";

		EmbeddedXADataSource aDataSource = new EmbeddedXADataSource();
		aDataSource.setDataSourceName("EmbeddedXADataSource");
		aDataSource.setUser("SillyDB");
		aDataSource.setPassword("SillyDB");
		aDataSource.setConnectionAttributes(dbAttributes);

		aDataSource.setDatabaseName(tmpDir.toString().replace("C:", "").replace('\\', '/').trim());

		System.out.println("DB Dir: " + tmpDir + " Created : " + createDB);
		try
		{
			Connection aConnection = aDataSource.getConnection();

			if (createDB)
			{
				loadSchema(aConnection, SCHEMA_DERBY);
				SillyDataHelper aHelper = new SillyDataHelper();
				aHelper.setupSilyMockData(aDataSource, oNumberOfRecords);
			}
			else if (varInsertNewData)
			{
				SillyDataHelper aHelper = new SillyDataHelper();
				aHelper.setupSilyMockData(aDataSource, oNumberOfRecords);
			}
			else
			{
				cleadData(aConnection);
			}
			aConnection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public void createMySQLDB(boolean varInsertNewData)
	{
		MysqlConnectionPoolDataSource aDataSource = new com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource();
		aDataSource.setPassword("web_user");
		aDataSource.setUser("web_user");
		aDataSource.setURL("jdbc:mysql://localhost/test");
		try
		{
			Connection aConnection = aDataSource.getConnection();

			SillyDataHelper aHelper = new SillyDataHelper();
			if (aHelper.dbCreated(aConnection) == false)
			{
				loadSchema(aConnection, SCHEMA_MySQL);
				aHelper.setupSilyMockData(aDataSource, oNumberOfRecords);
			}
			else if (varInsertNewData)
				aHelper.setupSilyMockData(aDataSource, oNumberOfRecords);
			else
				cleadData(aConnection);
			aConnection.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void cleadData(Connection varConnection)
	{
		SillyDAO aDAO = new SillyDAO(varConnection);
		int rc = aDAO.cleadData();
		System.out.println("Clearded data : " + rc + " records cleared, errorCode : " + aDAO.getErrorCode() + " errorMsg : " + aDAO.getErrorMessage());

	}

	public static void main(String[] args)
	{
		SetupBD aSetupBD = new SetupBD();
		// doMySQL();
		// aSetupBD.createDerbyDB();
		aSetupBD.createMySQLDB(false);
	}
}
