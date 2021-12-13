package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Partition
{
	private static final String FILD_NODE = "partition";
	private CBean oMapper;

	public CBean getMapper()
	{
		return oMapper;
	}

	public void setMapper(CBean varMapper)
	{
		oMapper = varMapper;
		oMapper.setTagName("mapper");
	}

	@Override
	public String toString()
	{
		return "Partition [oMapper=" + oMapper + ']';
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

		int aSubIndent = varIndent + 1;
		if (oMapper != null)
			aBuffer.append(oMapper.toXML(aSubIndent));
		aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));

		return (aBuffer.toString());
	}
}
