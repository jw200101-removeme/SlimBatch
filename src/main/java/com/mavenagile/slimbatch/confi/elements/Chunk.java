package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Chunk
{
	private static final String FILD_NODE = "chunk";
	private static final String FILD_ITEMS = "Items";
	private static final String FILD_SKIPS = "Skips";
	private static final String FILD_RETRYS = "Retrys";
	private int oItems;
	private int oSkips;
	private int oRetrys;
	private CBean oReader;
	private CBean oProcessor;
	private CBean oWriter;

	public int getItems()
	{
		return oItems;
	}

	public void setItems(int varItems)
	{
		oItems = varItems;
	}

	public int getSkips()
	{
		return oSkips;
	}

	public void setSkips(int varSkips)
	{
		oSkips = varSkips;
	}

	public int getRetrys()
	{
		return oRetrys;
	}

	public void setRetrys(int varRetrys)
	{
		oRetrys = varRetrys;
	}

	public CBean getReader()
	{
		return oReader;
	}

	public void setReader(CBean varReader)
	{
		oReader = varReader;
		oReader.setTagName("reader");
	}

	public CBean getProcessor()
	{
		return oProcessor;
	}

	public void setProcessor(CBean varProcessor)
	{
		oProcessor = varProcessor;
		oProcessor.setTagName("processor");
	}

	public CBean getWriter()
	{
		return oWriter;
	}

	public void setWriter(CBean varWriter)
	{
		oWriter = varWriter;
		oWriter.setTagName("writer");
	}

	@Override
	public String toString()
	{
		return "Chunk [oItems=" + oItems + ", oSkips=" + oSkips + ", oRetrys=" + oRetrys + ",\n oReader=" + oReader + ",\n oProcessor=" + oProcessor + ",\n oWriter=" + oWriter + ']';
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
		StringBuilder aBuffer = new StringBuilder(2048);

		aBuffer.append(XMLUtility.getBeginTag(FILD_NODE, varIndent, true));
		aBuffer.append(XMLUtility.getAttribute(FILD_ITEMS, oItems));
		aBuffer.append(XMLUtility.getAttribute(FILD_SKIPS, oSkips));
		aBuffer.append(XMLUtility.getAttribute(FILD_RETRYS, oRetrys));
		aBuffer.append(XMLUtility.TAG_END_MARK);
		int aSubIndent = varIndent + 1;
		if (oReader != null)
			aBuffer.append(oReader.toXML(aSubIndent));
		if (oProcessor != null)
			aBuffer.append(oProcessor.toXML(aSubIndent));
		if (oWriter != null)
			aBuffer.append(oWriter.toXML(aSubIndent));
		aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));

		return (aBuffer.toString());
	}
}
