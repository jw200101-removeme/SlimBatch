package com.mavenagile.slimbatch.confi.elements;

import java.util.ArrayList;
import java.util.List;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Job
{
	private static final String FILD_NODE = "job";
	private static final String FILD_ID = "ID";
	private String oID;
	private CProperties oProperties;
	private Listeners oListeners;
	private List<Step> oSteps;

	public String getID()
	{
		return oID;
	}

	public void setID(String varID)
	{
		oID = varID;
	}

	public CProperties getProperties()
	{
		return oProperties;
	}

	public void setProperties(CProperties varProperties)
	{
		oProperties = varProperties;
	}

	public Listeners getListeners()
	{
		return oListeners;
	}

	public void setListeners(Listeners varListeners)
	{
		oListeners = varListeners;
	}

	public List<Step> getSteps()
	{
		return oSteps;
	}

	public void setSteps(List<Step> varSteps)
	{
		oSteps = varSteps;
	}

	public void addStep(Step varStep)
	{
		if (oSteps == null)
			oSteps = new ArrayList<Step>();
		oSteps.add(varStep);
	}

	@Override
	public String toString()
	{
		return "Job [oID=" + oID + ",\n oProperties=" + oProperties + ",\n oListeners=" + oListeners + ",\n oSteps=" + oSteps + ']';
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
		aBuffer.append(XMLUtility.getAttribute(FILD_ID, oID));
		aBuffer.append(XMLUtility.TAG_END_MARK);

		int aSubIndent = varIndent + 1;
		if (oProperties != null)
			aBuffer.append(oProperties.toXML(aSubIndent));
		if (oListeners != null)
			aBuffer.append(oListeners.toXML(aSubIndent));
		if (oSteps != null)
		{
			for (Step aStep : oSteps)
				aBuffer.append(aStep.toXML(aSubIndent));
		}

		aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));

		return (aBuffer.toString());
	}
}
