package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Transaction
{
	private static final String FILD_NODE = "transaction";
	private static final String FILD_COMMITSIZE = "commitsize";
	private static final String FILD_DATASOURCE = "datasource";

	private int oCommitSize;
	private String oDataSource;

	public int getCommitSize()
	{
		return oCommitSize;
	}

	public void setCommitSize(int varCommitSize)
	{
		oCommitSize = varCommitSize;
	}

	public String getDataSource()
	{
		return oDataSource;
	}

	public void setDataSource(String varDataSource)
	{
		oDataSource = varDataSource;
	}

	/**
	 * Returns an XML representation of this object
	 * 
	 * @return String
	 */
	public String toXML()
	{
		return (toXML(0));
	}

	/**
	 * Returns an XML representation of this object
	 * 
	 * @return String
	 */
	public String toXML(int varIndent)
	{
		StringBuilder aBuffer = new StringBuilder(254);
		aBuffer.append(XMLUtility.getBeginTag(FILD_NODE, varIndent, true));
		aBuffer.append(XMLUtility.getAttribute(FILD_COMMITSIZE, oCommitSize));
		aBuffer.append(XMLUtility.getAttribute(FILD_DATASOURCE, oDataSource));
		aBuffer.append(XMLUtility.TAG_END_END_MARK);
		return (aBuffer.toString());
	}
}
