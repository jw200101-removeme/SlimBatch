package com.mavenagile.slimbatch.confi.elements;

import java.util.ArrayList;

import com.mavenagile.slimbatch.utility.XMLUtility;

/**
 * @author Root
 *
 */
@Deprecated
public class CBeans extends ArrayList<CBean>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FILD_NODE = "beans";

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
		StringBuffer aBuffer = new StringBuffer(210);

		if (isEmpty() == false)
		{
			int aSubIndent = varIndent + 1;

			aBuffer.append(XMLUtility.getBeginTag(FILD_NODE, varIndent));
			if (isEmpty() == false)
			{
				for (CBean aBean : this)
					aBuffer.append(aBean.toXML(aSubIndent));
			}
			aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));
		}
		return (aBuffer.toString());
	}
}
