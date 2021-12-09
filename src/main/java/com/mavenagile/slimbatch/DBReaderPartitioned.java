package com.mavenagile.slimbatch;

import java.sql.Connection;

public interface DBReaderPartitioned extends PartitionedReader
{

	public void setConnection(Connection varConnection);

}
