package com.mavenagile.slimbatch.utility;
/*
 * Created on May 27, 2006
 */

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
  
 */
public final class XMLUtility
{
	public static final String RCS_XMLUtility_java = "$Id";
	public static final char TEXT_NL = '\n';
	public static final String EMPTY_STRING = "";

	private static final char C_CHARAMPERSAND = '&';
	private static final char C_CHARLESSTHAN = '<';
	private static final char C_CHARGREATERTHAN = '>';
	private static final char C_CHARAPOSTROPHE = '\'';
	private static final char C_CHARDOUBLEQUOTE = '"';

	private static final String STR_XML_Ampersand = "&amp;".intern();
	private static final String STR_XML_LessThan = "&lt;".intern();
	private static final String STR_XML_GreaterThan = "&gt;".intern();
	private static final String STR_XML_Apostrophe = "&apos;".intern();
	private static final String STR_XML_DoubleQuote = "&quot;".intern();

	private static final String[] TABS = { EMPTY_STRING, "\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t\t\t", "\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t\t" };

	public static final String XML_FILE_HEADER_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".intern();
	public static final String TAG_BEGIN_TAG_MARK = "<".intern();
	public static final String TAG_BEGIN_CLOSE_TAG_MARK = "</".intern();
	public static final String TAG_END_MARK = ">".intern();
	public static final String TAG_END_END_MARK = " />".intern();
	public static final String END_ATTRIBUTE_VALUE = "\"".intern();
	public static final String BEGIN_ATTRIBUTE_VALUE = "=\"".intern();
	public static final char SPACE = ' ';

	private XMLUtility()
	{
		/*
		 * do nothing
		 */
	}

	public static boolean isNull(String varStr)
	{
		if (varStr == null || varStr.length() <= 0 || varStr.trim().length() <= 0)
			return (true);
		return (false);
	}

	/**
	 * Converts string to XML text Entity Displays As Character Value &amp; & &#38;#38; &lt; < &#38;#60; &gt; > &#62; &apos; ' &#39; &quot; " &#34;
	 *
	 * <pre>
	 * "   &quot;
	 * '   &apos;
	 * <   &lt;
	 * >   &gt;
	 * &   &amp;
	 * </pre>
	 *
	 * @param varBuffer
	 * @return String
	 *
	 */
	public static String toXMLString(String varBuffer)
	{
		if (XMLUtility.isNull(varBuffer))
			return (null);
		int j = varBuffer.length();
		StringBuilder newBuffer = new StringBuilder(j + 10);
		for (int i = 0; i < j; i++)
			if (varBuffer.charAt(i) == XMLUtility.C_CHARLESSTHAN)
				newBuffer.append(XMLUtility.STR_XML_LessThan);
			else if (varBuffer.charAt(i) == XMLUtility.C_CHARGREATERTHAN)
				newBuffer.append(XMLUtility.STR_XML_GreaterThan);
			else if (varBuffer.charAt(i) == XMLUtility.C_CHARAPOSTROPHE)
				newBuffer.append(XMLUtility.STR_XML_Apostrophe);
			else if (varBuffer.charAt(i) == XMLUtility.C_CHARDOUBLEQUOTE)
				newBuffer.append(XMLUtility.STR_XML_DoubleQuote);
			else if (varBuffer.charAt(i) == XMLUtility.C_CHARAMPERSAND && varBuffer.indexOf(XMLUtility.STR_XML_Ampersand, i) == -1//
					&& varBuffer.indexOf(XMLUtility.STR_XML_LessThan, i) == -1 && varBuffer.indexOf(XMLUtility.STR_XML_GreaterThan, i) == -1 && //
					varBuffer.indexOf(XMLUtility.STR_XML_Apostrophe, i) == -1 && varBuffer.indexOf(XMLUtility.STR_XML_DoubleQuote, i) == -1)
				newBuffer.append(XMLUtility.STR_XML_Ampersand);
			else
				newBuffer.append(varBuffer.charAt(i));
		return (newBuffer.toString());
	}

	public static String getTabs(int idx)
	{
		if (idx < 0 || idx > XMLUtility.TABS.length - 1)
			return (XMLUtility.TABS[0]);
		return (XMLUtility.TABS[idx]);
	}

	public static String getBeginTag(String varTagName, int varIndent, boolean varOpenTag)
	{
		if (varTagName == null || varTagName.length() <= 0)
			return (null);
		StringBuilder aBuffer = new StringBuilder(varTagName.length() + 2);
		if (varIndent >= 0)
			aBuffer.append(TEXT_NL).append(XMLUtility.getTabs(varIndent));
		aBuffer.append(XMLUtility.TAG_BEGIN_TAG_MARK).append(varTagName);
		if (!varOpenTag)
			aBuffer.append(XMLUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String getBeginTag(String varTagName)
	{
		return (XMLUtility.getBeginTag(varTagName, -1, false));
	}

	public static String getBeginTag(String varTagName, boolean varOpenTag)
	{
		return (XMLUtility.getBeginTag(varTagName, -1, varOpenTag));
	}

	public static String getBeginTag(String varTagName, int varIndent)
	{
		return (XMLUtility.getBeginTag(varTagName, varIndent, false));
	}

	public static String getEndTag(String varTagName)
	{
		return (XMLUtility.getEndTag(varTagName, -1));
	}

	public static String getEndTag(String varTagName, int varIndent)
	{
		if (varTagName == null || varTagName.length() <= 0)
			return (null);
		StringBuilder aBuffer = new StringBuilder(varTagName.length() + 3);
		if (varIndent >= 0)
			aBuffer.append(TEXT_NL).append(XMLUtility.getTabs(varIndent));
		aBuffer.append(XMLUtility.TAG_BEGIN_CLOSE_TAG_MARK).append(varTagName).append(XMLUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String getTag(String varTagName, Object varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, varValue, varIndent, varNoTagIfNull, true));
	}

	public static String getTag(String varTagName, Object varValue, int varIndent, boolean varNoTagIfNull, boolean varEscapeXMLValue)
	{
		if ((varNoTagIfNull && varValue == null) || varTagName == null)
			return ("");
		StringBuilder aBuffer = new StringBuilder();
		aBuffer.append(TEXT_NL).append(XMLUtility.getTabs(varIndent));
		aBuffer.append(XMLUtility.TAG_BEGIN_TAG_MARK).append(varTagName).append(XMLUtility.TAG_END_MARK);
		if (varValue != null && varEscapeXMLValue)
			aBuffer.append(XMLUtility.toXMLString(varValue.toString()));
		else if (varValue != null)
			aBuffer.append(varValue);

		aBuffer.append(XMLUtility.TAG_BEGIN_CLOSE_TAG_MARK).append(varTagName).append(XMLUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String getTag(String varTagName, char varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, short varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, int varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, float varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, double varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, boolean varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getTag(String varTagName, long varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (XMLUtility.getTag(varTagName, String.valueOf(varValue), varIndent, varNoTagIfNull, false));
	}

	public static String getAttribute(String varAttributName, String varAttributValue)
	{
		if (XMLUtility.isNull(varAttributValue))
			return ("");
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, int varAttributValue)
	{
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, double varAttributValue)
	{
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, float varAttributValue)
	{
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, long varAttributValue)
	{
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, boolean varAttributValue)
	{
		return (XMLUtility.getAttributeBuffer(varAttributName, varAttributValue));
	}

	private static String getAttributeBuffer(String varAttributName, Object varObject)
	{
		StringBuilder aBuffer = new StringBuilder();
		aBuffer.append(SPACE).append(varAttributName).append("=\"").append(varObject).append('\"');
		return (aBuffer.toString());
	}

	public static String getTagsChildNodeValue(Node varNode, String varTagName, int varTagIndex, int varChildIndex, String varDefaultValue)
	{
		return (XMLUtility.getTagsChildNodeValue((Element) varNode, varTagName, varTagIndex, varChildIndex, varDefaultValue));
	}

	public static String getNamedAttributeValue(Node varElement, String varAttributName, String varDefaultValue)
	{
		if (varElement != null && varElement.getAttributes() != null && varElement.getAttributes().getNamedItem(varAttributName) != null)
		{
			return (varElement.getAttributes().getNamedItem(varAttributName).getNodeValue());
		}
		return (varDefaultValue);
	}

	public static String getTagsChildNodeValue(Element varElement, String varTagName, int varTagIndex, int varChildIndex, String varDefaultValue)
	{
		if (varElement != null && varElement.getElementsByTagName(varTagName) != null && varElement.getElementsByTagName(varTagName).item(varTagIndex) != null
				&& varElement.getElementsByTagName(varTagName).item(varTagIndex).getChildNodes().item(varChildIndex) != null)
		{
			return (varElement.getElementsByTagName(varTagName).item(varTagIndex).getChildNodes().item(varChildIndex).getNodeValue());
		}
		return (varDefaultValue);
	}

	/**
	 *
	 * varNode.getChildNodes().item(varChildIndex).getNodeValue()
	 *
	 * @param varNode
	 * @param varChildIndex
	 * @param varDefaultValue
	 * @return String
	 *
	 */
	public static String getNodeChildValue(Node varNode, int varChildIndex, String varDefaultValue)
	{
		if (varNode != null && varNode.getChildNodes() != null && varNode.getChildNodes().item(varChildIndex) != null)
		{
			return (varNode.getChildNodes().item(varChildIndex).getNodeValue());
		}
		return (varDefaultValue);
	}

}
