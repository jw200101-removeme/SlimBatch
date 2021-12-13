package com.mavenagile.slimbatch;

import java.sql.Connection;

public interface DBReader extends Reader
{
	public void setConnection(Connection varConnection);
}
