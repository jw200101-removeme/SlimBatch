/*
 * Created on Jul 1, 2007
 *
 */
package com.mavenagile.slimbatch.utility;

import java.util.StringTokenizer;

/**
 
 */
public final class JavaClassHelper
{
	public static final String RCS_JavaClassHelper_java = "$Id";

	private JavaClassHelper()
	{
		/*
		 * do nothing
		 */
	}

	private static final String[] KEYWORDS = { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp",
			"volatile", "const", "float", "native", "super", "while" };

	public static String makeMemberName(String varValue, String varPrefix)
	{
		if (XMLUtility.isNull(varValue) || XMLUtility.isNull(varPrefix))
			return (null);
		StringBuilder aBuffer = new StringBuilder();
		if (varValue.startsWith(varPrefix) && varValue.length() > varPrefix.length() && varValue.charAt(varPrefix.length()) == String.valueOf(varValue.charAt(varPrefix.length())).toUpperCase().charAt(0))
			return (varValue);
		aBuffer.append(varPrefix);
		aBuffer.append(String.valueOf(varValue.charAt(0)).toUpperCase());
		aBuffer.append(varValue.substring(1));
		return (aBuffer.toString());
	}

	public static String makeMethodName(String varValue, String varPrefix, String varRemovePrefix)
	{
		if (XMLUtility.isNull(varValue) || XMLUtility.isNull(varPrefix))
			return (null);
		StringBuilder aBuffer = new StringBuilder();

		if (varValue.startsWith(varRemovePrefix))
		{
			aBuffer.append(varValue.substring(varRemovePrefix.length()));
			aBuffer.setCharAt(0, String.valueOf(aBuffer.charAt(0)).toUpperCase().charAt(0));
		}
		else
		{
			aBuffer.append(varValue);
			aBuffer.setCharAt(0, String.valueOf(varValue.charAt(0)).toUpperCase().charAt(0));
		}
		if (!varValue.startsWith(varPrefix))
			aBuffer.insert(0, varPrefix);

		return (aBuffer.toString());
	}

	public static String makeMethodName(String varValue, String varPrefix)
	{
		if (XMLUtility.isNull(varValue) || XMLUtility.isNull(varPrefix))
			return (null);
		StringBuilder aBuffer = new StringBuilder(varValue);
		aBuffer.setCharAt(0, String.valueOf(varValue.charAt(0)).toUpperCase().charAt(0));
		if (!varValue.startsWith(varPrefix))
			aBuffer.insert(0, varPrefix);
		return (aBuffer.toString());
	}

	public static String makeClassName(String varValue, String varPostfix)
	{
		if (XMLUtility.isNull(varValue))
			return (null);
		StringBuilder aBuffer = new StringBuilder(varValue);
		aBuffer.setCharAt(0, String.valueOf(varValue.charAt(0)).toUpperCase().charAt(0));
		if (varPostfix != null && !varValue.endsWith(varPostfix))
			aBuffer.append(varPostfix);
		return (aBuffer.toString());
	}

	public static boolean isKeyword(String varIdentifier)
	{
		if (XMLUtility.isNull(varIdentifier))
			return (false);
		for (int i = JavaClassHelper.KEYWORDS.length - 1; i >= 0; i--)
			if (varIdentifier.equals(JavaClassHelper.KEYWORDS[i]))
				return (false);
		return (true);
	}

	public static boolean isJavaLangMember(String varIdentifier)
	{
		try
		{
			Class.forName("java.lang." + varIdentifier);
			return (true);
		}
		catch (Exception e)
		{
			return (false);
		}
	}

	/**
	 * Valid Java identifier is starts with a alpha or '$' or '_' contains only alpha and '$' and '_' not a keyword not a reserved keyword not a java.lang member
	 *
	 * @param varIdentifier
	 * @return boolean
	 *
	 */
	public static boolean isValidJavaIdentifier(String varIdentifier)
	{
		if (!JavaClassHelper.isKeyword(varIdentifier))
			return (false);
		if (Character.isJavaIdentifierStart(varIdentifier.charAt(0)))
		{
			for (int i = varIdentifier.length() - 1; i > 0; i--)
				if (!Character.isJavaIdentifierPart(varIdentifier.charAt(i)) || JavaClassHelper.isJavaLangMember(varIdentifier))
					return (false);
		}
		else
			return (false);
		return (true);
	}

	public static boolean isValidPackageName(String varPackage)
	{
		if (XMLUtility.isNull(varPackage) || varPackage.charAt(0) == '.' || varPackage.indexOf("..") != -1 || varPackage.charAt(0) == ' ' || varPackage.charAt(varPackage.length() - 1) == '.' || varPackage.charAt(varPackage.length() - 1) == ' ')
			return (false);
		StringTokenizer aTokenizer = new StringTokenizer(varPackage.trim(), ".");
		String token;
		while (aTokenizer.hasMoreTokens())
		{
			token = aTokenizer.nextToken();
			if (!JavaClassHelper.isValidJavaIdentifier(token))
				return (false);
		}
		return (true);
	}

	/**
	 * Cleans the identifier name by replacing ' ', '.', and '-' by '_'
	 *
	 * @param varIdentifier
	 * @param varPrefix
	 * @return
	 *
	 *         Creation date: (May 2, 2008 2:54:15 PM)
	 */
	public static String makeJavaIdentifier(String varIdentifier, String varPrefix)
	{
		if (XMLUtility.isNull(varIdentifier) || XMLUtility.isNull(varPrefix))
			return (null);
		if (varIdentifier.startsWith(varPrefix))
			return (varIdentifier);
		StringBuilder aBuffer = new StringBuilder(varPrefix.length() + varIdentifier.length());
		aBuffer.append(varPrefix).append(varIdentifier.trim().toUpperCase());
		for (int i = aBuffer.length() - 1; i >= 0; i--)
		{
			if (aBuffer.charAt(i) == '.' || aBuffer.charAt(i) == '-' || aBuffer.charAt(i) == ' ')
				aBuffer.setCharAt(i, '_');
		}
		if (JavaClassHelper.isValidJavaIdentifier(aBuffer.toString()))
			return (aBuffer.toString());
		return (null);
	}
}
