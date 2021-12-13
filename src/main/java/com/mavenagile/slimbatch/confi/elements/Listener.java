package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Listener
{
	private static final String FILD_NODE = "listener";
	private static final String FILD_REFERENCE = "ref";
	private String oReference;
	private boolean oResolved;

	public String getReference()
	{
		return oReference;
	}

	public void setReference(String varReference)
	{
		oReference = varReference;
	}

	public void setRef(String varReference)
	{
		oReference = varReference;
	}

	public boolean isResolved()
	{
		return oResolved;
	}

	public void setResolved(boolean varResolved)
	{
		oResolved = varResolved;
	}

	@Override
	public String toString()
	{
		return "Listener [oReference=" + oReference + ", oResolved=" + oResolved + ']';
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
		aBuffer.append(XMLUtility.getAttribute(FILD_REFERENCE, oReference));
		aBuffer.append(XMLUtility.TAG_END_END_MARK);

		return (aBuffer.toString());
	}
}
