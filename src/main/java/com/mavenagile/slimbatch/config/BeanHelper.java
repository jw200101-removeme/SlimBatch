package com.mavenagile.slimbatch.config;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanHelper
{
	private static Log LOGGER = LogFactory.getLog(BeanHelper.class);

	public static boolean isNull(String varStr)
	{
		if (varStr == null || varStr.length() <= 0 || varStr.trim().length() <= 0)
			return (true);
		return (false);
	}

	public static Class<?> findClass(String varClassName)
	{
		try
		{
			ClassLoader aLoader = Thread.currentThread().getContextClassLoader();
			Class<?> aClassDefinition;
			if (aLoader != null)
				aClassDefinition = Class.forName(varClassName, false, aLoader);
			else
				aClassDefinition = Class.forName(varClassName);
			return (aClassDefinition);
		}
		catch (Exception e)
		{
			return (null);
		}
	}

	public static Method getMethod(Class<?> varTheClass, String varMethodName, String varArgumetnType) throws Exception
	{
		Class<?> anArgumentTypes[] = null;
		if ("string".equals(varArgumetnType))
			anArgumentTypes = new Class[] { String.class };
		else if ("int".equals(varArgumetnType))
			anArgumentTypes = new Class[] { int.class };
		else if ("double".equals(varArgumetnType))
			anArgumentTypes = new Class[] { double.class };
		else if ("boolean".equals(varArgumetnType))
			anArgumentTypes = new Class[] { boolean.class };

		return (getMethod(varTheClass, varMethodName, anArgumentTypes));
	}

	public static Method getMethod(Class<?> varTheClass, String varMethodName, Class<?> varArgumentTypes[]) throws Exception
	{
		Class<?> tmpRootClass = varTheClass;
		Method theMethod = null;
		while (tmpRootClass != null)
		{
			try
			{
				theMethod = tmpRootClass.getDeclaredMethod(varMethodName, varArgumentTypes);
				return (theMethod);
			}
			catch (Exception e)
			{
				// eat up exception on purpose
			}
			// give up if we reach as far as Object
			if (tmpRootClass == Object.class)
				break;
			tmpRootClass = tmpRootClass.getSuperclass();
		}
		LOGGER.error(varTheClass.getSimpleName() + "::getMethod: no such Method [" + varMethodName + "]  in class  " + varTheClass.getName() + " or its super class");
		throw new Exception("Configuration error : " + varTheClass.getSimpleName() + "::getMethod: no such Method [" + varMethodName + "]  in class  " + varTheClass.getName() + " or its super class");
	}

	public static Method findMethod(Object varObject, String varMethod, Object varParameterType)
	{
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("	Finding a method named [" + varMethod + "] in class [" + varObject.getClass() + "] with parm[" + (varParameterType != null ? varParameterType.getClass().getName() : "none") + "]");
		Method aMethods[] = varObject.getClass().getMethods();

		for (int i = aMethods.length - 1; i >= 0; i--)
		{
			if (aMethods[i].getName().equalsIgnoreCase(varMethod))
			{
				if (varParameterType == null)
					return (aMethods[i]);
				if (aMethods[i].getParameterTypes() != null && aMethods[i].getParameterTypes().length == 1 && aMethods[i].getParameterTypes()[0].isAssignableFrom(varParameterType.getClass()))
					return (aMethods[i]);
			}
		}
		return (null);
	}

	public static String makeMethodName(String varValue, String varPrefix)
	{
		if (isNull(varValue) || isNull(varPrefix))
			return (null);
		StringBuilder aBuffer = new StringBuilder(varValue);
		aBuffer.setCharAt(0, String.valueOf(varValue.charAt(0)).toUpperCase().charAt(0));
		if (varValue.startsWith(varPrefix) == false)
			aBuffer.insert(0, varPrefix);
		return (aBuffer.toString());
	}

	public static boolean applySetMethod(Object varObject, String varMethodName, Object varValue) throws Exception
	{
		Method aMethod = findMethod(varObject, makeMethodName(varMethodName, "set"), varValue);
		if (aMethod == null)
			aMethod = findMethod(varObject, makeMethodName(varMethodName, "set"), null);
		if (aMethod == null)
		{
			if (LOGGER.isTraceEnabled())
				LOGGER.trace("Undefined method set" + varMethodName + ", add" + varMethodName + " or add" + " for class " + varObject.getClass().getName() + " value type : " + varValue.getClass());
			return (false);
		}
		Class<?> parameterType = aMethod.getParameterTypes()[0];

		if (LOGGER.isTraceEnabled())
			LOGGER.trace("	Invoke method [" + aMethod.getName() + "] : parm[" + parameterType + "] value [" + varValue + "] on class [" + varObject.getClass() + "]");
		try
		{
			boolean isValueString = varValue.getClass().getName().equals("java.lang.String");
			if (parameterType == String.class)
				aMethod.invoke(varObject, new Object[] { varValue });
			else if (isValueString == true)
			{
				if (parameterType == boolean.class)
					aMethod.invoke(varObject, new Object[] { Boolean.valueOf((String) varValue) });
				else if (parameterType == long.class)
					aMethod.invoke(varObject, new Object[] { Long.valueOf((String) varValue) });
				else if (parameterType == short.class)
					aMethod.invoke(varObject, new Object[] { Short.valueOf((String) varValue) });
				else if (parameterType == int.class)
					aMethod.invoke(varObject, new Object[] { Integer.valueOf((String) varValue) });
				else if (parameterType == float.class)
					aMethod.invoke(varObject, new Object[] { Float.valueOf((String) varValue) });
				else if (parameterType == double.class)
					aMethod.invoke(varObject, new Object[] { Double.valueOf((String) varValue) });
				else
					aMethod.invoke(varObject, new Object[] { varValue });
			}
			else
				aMethod.invoke(varObject, new Object[] { varValue });
			return (true);

		}
		catch (Exception e)
		{
			LOGGER.debug("Invalid method : Class [" + varObject.getClass().getName() + "] method [" + aMethod.getName() + "] parameterType [" + (aMethod.getParameterTypes() != null ? aMethod.getParameterTypes()[0].getName() : "none")
					+ "] argument type [" + varValue.getClass().getName() + "] argument object : " + varValue, e);
		}
		return (false);
	}
}
