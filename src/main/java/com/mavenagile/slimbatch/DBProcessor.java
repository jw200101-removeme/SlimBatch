package com.mavenagile.slimbatch;

import java.sql.Connection;

public abstract class DBProcessor extends Processor
{
	protected Connection oConnection;

	public void setConnection(Connection varConnection)
	{
		oConnection = varConnection;
	}
}
