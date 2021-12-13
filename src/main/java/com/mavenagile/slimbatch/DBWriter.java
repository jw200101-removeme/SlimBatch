package com.mavenagile.slimbatch;

import java.sql.Connection;

public interface DBWriter extends Writer
{

	public void setConnection(Connection varConnection);
}
