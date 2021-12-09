package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.utility.XMLUtility;

public class Step
{

	private static final String FILD_NODE = "step";
	private static final String FILD_ID = "id";
	private String oID;
	private Chunk oChunk;
	private Partition oPartition;
	private Transaction oTransaction;

	public String getID()
	{
		return oID;
	}

	public void setID(String varID)
	{
		oID = varID;
	}

	public Chunk getChunk()
	{
		return oChunk;
	}

	public void setChunk(Chunk varChunk)
	{
		oChunk = varChunk;
	}

	public Partition getPartition()
	{
		return oPartition;
	}

	public void setPartition(Partition varPartition)
	{
		oPartition = varPartition;
	}

	public Transaction getTransaction()
	{
		return oTransaction;
	}

	public void setTransaction(Transaction varTransaction)
	{
		oTransaction = varTransaction;
	}

	@Override
	public String toString()
	{
		return "Step [oID=" + oID + ", oChunk=" + oChunk + ", oPartition=" + oPartition + ", oTransaction=" + oTransaction + "]";
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
		StringBuffer aBuffer = new StringBuffer(254);

		aBuffer.append(XMLUtility.getBeginTag(FILD_NODE, varIndent, true));
		aBuffer.append(XMLUtility.getAttribute(FILD_ID, oID));
		aBuffer.append(XMLUtility.TAG_END_MARK);

		int aSubIndent = varIndent + 1;
		if (oTransaction != null)
			aBuffer.append(oTransaction.toXML(aSubIndent));
		if (oChunk != null)
			aBuffer.append(oChunk.toXML(aSubIndent));
		if (oPartition != null)
			aBuffer.append(oPartition.toXML(aSubIndent));
		aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));

		return (aBuffer.toString());
	}
}
