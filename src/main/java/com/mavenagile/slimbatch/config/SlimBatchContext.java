package com.mavenagile.slimbatch.config;

import javax.sql.DataSource;

import com.mavenagile.slimbatch.confi.elements.CProperties;
import com.mavenagile.slimbatch.confi.elements.Job;

/**
 * @author root
 *
 *         A helper class to obtain items
 * 
 */
public class SlimBatchContext
{
	public static SlimBatch getInstance()
	{
		return (SlimBatch.getInstance());
	}

	public static Object getBean(String varBeanID) throws Exception
	{
		return (SlimBatch.getInstance().getConfiguration().getBean(varBeanID, null));
	}

	public static Object getBean(String varBeanID, CProperties varCustomProperties) throws Exception
	{
		return (SlimBatch.getInstance().getConfiguration().getBean(varBeanID, varCustomProperties));
	}

	public static DataSource getDatasource(String varDatasourceID) throws Exception
	{
		return (SlimBatch.getInstance().getConfiguration().getDataSource(varDatasourceID));
	}

	public static Job getJob(String varID)
	{
		return (SlimBatch.getInstance().getConfiguration().getJob(varID));
	}
}
