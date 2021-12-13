package demo.mavenagile.slimbatch.dataaccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataAccessUtility
{
	/**
	 * RCS String
	 */
	public static final String TABS[] = { "", "\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t\t\t", "\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t\t" };

	public static final char C_CHARAMPERSAND = '&';
	public static final char C_CHARLESSTHAN = '<';
	public static final char C_CHARGREATERTHAN = '>';
	public static final char C_CHARAPOSTROPHE = '\'';
	public static final char C_CHARDOUBLEQUOTE = '"';

	public static final String STR_XML_Ampersand = "&amp;".intern();
	public static final String STR_XML_LessThan = "&lt;".intern();
	public static final String STR_XML_GreaterThan = "&gt;".intern();
	public static final String STR_XML_Apostrophe = "&apos;".intern();
	public static final String STR_XML_DoubleQuote = "&quot;".intern();
	public static final char TEXT_NL = '\n';
	public static final String TAG_BEGIN_TAG_MARK = "<".intern();
	public static final String TAG_BEGIN_CLOSE_TAG_MARK = "</".intern();
	public static final String TAG_END_END_MARK = " />".intern();
	public static final String TAG_END_MARK = ">".intern();
	public static final String END_ATTRIBUTE_VALUE = "\"".intern();
	public static final String BEGIN_ATTRIBUTE_VALUE = "=\"".intern();

	public final static String rcs_DataAccessLogger_java = "$Id$";
	private static Log oLogger = LogFactory.getLog(DataAccessUtility.class);

	public static Log getLogger()
	{
		return (DataAccessUtility.oLogger);
	}

	public static String getTabs(int idx)
	{
		if (idx < 0 || idx > DataAccessUtility.TABS.length - 1)
			return (DataAccessUtility.TABS[0]);
		return (DataAccessUtility.TABS[idx]);
	}

	public static String getBeginTag(String varTagName, boolean varOpenTag)
	{
		return (DataAccessUtility.getBeginTag(varTagName, -1, varOpenTag));
	}

	public static String getBeginTag(String varTagName, int varIndent)
	{
		return (DataAccessUtility.getBeginTag(varTagName, varIndent, false));
	}

	public static String getBeginTag(String varTagName, int varIndent, boolean varOpenTag)
	{
		if (varTagName == null || varTagName.length() <= 0)
			return (null);
		StringBuilder aBuffer = new StringBuilder(varTagName.length() + 2);
		if (varIndent >= 0)
			aBuffer.append(DataAccessUtility.TEXT_NL).append(DataAccessUtility.getTabs(varIndent));
		aBuffer.append(DataAccessUtility.TAG_BEGIN_TAG_MARK).append(varTagName);
		if (varOpenTag == false)
			aBuffer.append(DataAccessUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String getEndTag(String varTagName, int varIndent)
	{
		if (varTagName == null || varTagName.length() <= 0)
			return (null);
		StringBuilder aBuffer = new StringBuilder(varTagName.length() + 3);
		if (varIndent >= 0)
			aBuffer.append(DataAccessUtility.TEXT_NL).append(DataAccessUtility.getTabs(varIndent));
		aBuffer.append(DataAccessUtility.TAG_BEGIN_CLOSE_TAG_MARK).append(varTagName).append(DataAccessUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String getTag(String varTagName, Object varValue, int varIndent, boolean varNoTagIfNull)
	{
		return (DataAccessUtility.getTag(varTagName, varValue, varIndent, varNoTagIfNull, true));
	}

	public static String getTag(String varTagName, Object varValue, int varIndent, boolean varNoTagIfNull, boolean varEscapeXMLValue)
	{
		if ((varNoTagIfNull == true && varValue == null) || varTagName == null)
			return ("");
		StringBuilder aBuffer = new StringBuilder();
		aBuffer.append(DataAccessUtility.TEXT_NL).append(DataAccessUtility.getTabs(varIndent));
		aBuffer.append(DataAccessUtility.TAG_BEGIN_TAG_MARK).append(varTagName).append(DataAccessUtility.TAG_END_MARK);
		if (varValue != null && varEscapeXMLValue == true)
			aBuffer.append(DataAccessUtility.toXMLString(varValue.toString()));
		else if (varValue != null)
			aBuffer.append(varValue);

		aBuffer.append(DataAccessUtility.TAG_BEGIN_CLOSE_TAG_MARK).append(varTagName).append(DataAccessUtility.TAG_END_MARK);
		return (aBuffer.toString());
	}

	public static String toXMLString(String varBuffer)
	{
		if (DataAccessUtility.isNull(varBuffer))
			return (null);
		int j = varBuffer.length();
		StringBuilder newBuffer = new StringBuilder(j + 10);
		for (int i = 0; i < j; i++)
			if (varBuffer.charAt(i) == DataAccessUtility.C_CHARLESSTHAN)
				newBuffer.append(DataAccessUtility.STR_XML_LessThan);
			else if (varBuffer.charAt(i) == DataAccessUtility.C_CHARGREATERTHAN)
				newBuffer.append(DataAccessUtility.STR_XML_GreaterThan);
			else if (varBuffer.charAt(i) == DataAccessUtility.C_CHARAPOSTROPHE)
				newBuffer.append(DataAccessUtility.STR_XML_Apostrophe);
			else if (varBuffer.charAt(i) == DataAccessUtility.C_CHARDOUBLEQUOTE)
				newBuffer.append(DataAccessUtility.STR_XML_DoubleQuote);
			else if (varBuffer.charAt(i) == DataAccessUtility.C_CHARAMPERSAND && varBuffer.indexOf(DataAccessUtility.STR_XML_Ampersand, i) == -1 && varBuffer.indexOf(DataAccessUtility.STR_XML_LessThan, i) == -1
					&& varBuffer.indexOf(DataAccessUtility.STR_XML_GreaterThan, i) == -1 && varBuffer.indexOf(DataAccessUtility.STR_XML_Apostrophe, i) == -1 && varBuffer.indexOf(DataAccessUtility.STR_XML_DoubleQuote, i) == -1)
				newBuffer.append(DataAccessUtility.STR_XML_Ampersand);
			else
				newBuffer.append(varBuffer.charAt(i));
		return (newBuffer.toString());
	}

	public static String getAttribute(String varAttributName, String varAttributValue)
	{
		if (isNull(varAttributValue))
			return ("");
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, int varAttributValue)
	{
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, double varAttributValue)
	{
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, float varAttributValue)
	{
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, long varAttributValue)
	{
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	public static String getAttribute(String varAttributName, boolean varAttributValue)
	{
		return (getAttributeBuffer(varAttributName, varAttributValue));
	}

	private static String getAttributeBuffer(String varAttributName, Object varObject)
	{
		StringBuilder aBuffer = new StringBuilder();
		aBuffer.append(" ").append(varAttributName).append("=\"").append(varObject).append("\"");
		return (aBuffer.toString());
	}

	public static boolean isNull(String varStr)
	{
		if (varStr == null || varStr.length() <= 0 || varStr.trim().length() <= 0)
			return (true);
		return (false);
	}
}
