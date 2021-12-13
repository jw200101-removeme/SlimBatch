package com.mavenagile.slimbatch.confi.elements;

import java.util.ArrayList;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Listeners extends ArrayList<Listener>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FILD_NODE = "listeners";

	@Override
	public String toString()
	{
		return (toXML(0));
	}

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
		StringBuilder aBuffer = new StringBuilder(210);

		if (isEmpty() == false)
		{
			int subIndent = varIndent + 1;

			aBuffer.append(XMLUtility.getBeginTag(FILD_NODE, varIndent));
			for (Listener aListener : this)
				aBuffer.append(aListener.toXML(subIndent));
			aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));
		}
		return (aBuffer.toString());
	}
}
