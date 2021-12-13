package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class CProperty
{
	private static final String FILD_NODE = "property";
	private static final String FILD_NAME = "name";
	private static final String FILD_TYPE = "type";
	private static final String FILD_VALUE = "value";
	private String oName;
	private String oType;
	private String oValue;

	public CProperty(String varName)
	{
		oName = varName;
	}

	public String getName()
	{
		return oName;
	}

	public void setName(String varName)
	{
		oName = varName;
	}

	public String getType()
	{
		return oType;
	}

	public void setType(String varType)
	{
		oType = varType;
	}

	public String getValue()
	{
		return oValue;
	}

	public void setValue(String varValue)
	{
		oValue = varValue;
	}

	@Override
	public String toString()
	{
		return "\nCProperty [oName=" + oName + ", oType=" + oType + ", oValue=" + oValue + ']';
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
		aBuffer.append(XMLUtility.getAttribute(FILD_NAME, oName));
		aBuffer.append(XMLUtility.getAttribute(FILD_TYPE, oType));
		aBuffer.append(XMLUtility.getAttribute(FILD_VALUE, oValue));
		aBuffer.append(XMLUtility.TAG_END_END_MARK);
		return (aBuffer.toString());
	}
}
