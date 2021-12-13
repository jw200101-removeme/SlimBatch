package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Include
{
	private static final String FILD_NODE = "include";
	private static final String FILD_RESOURCE = "resource";
	private String oTagName = FILD_NODE;

	private String oResource;
	private boolean oFetched;

	public String getTagName()
	{
		return oTagName;
	}

	public void setTagName(String varTagName)
	{
		oTagName = varTagName;
	}

	public String getResource()
	{
		return oResource;
	}

	public void setResource(String varResource)
	{
		oResource = varResource;
	}

	public boolean isFetched()
	{
		return oFetched;
	}

	public void setFetched(boolean varFetched)
	{
		oFetched = varFetched;
	}

	@Override
	public String toString()
	{
		return "Include [oResource=" + oResource + ']';
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

		aBuffer.append(XMLUtility.getBeginTag(oTagName, varIndent, true));
		aBuffer.append(XMLUtility.getAttribute(FILD_RESOURCE, oResource));
		aBuffer.append(XMLUtility.TAG_END_END_MARK);
		return (aBuffer.toString());
	}
}
