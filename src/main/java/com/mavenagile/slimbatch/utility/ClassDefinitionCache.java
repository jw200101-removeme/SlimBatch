package com.mavenagile.slimbatch.utility;

/*
 * Created on Sep 3, 2004
 *
 *
 */

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ClassDefinitionCache (class constructor cache) is used to cache a class a definition. This saves same time since a class constructor is called repeatedly.
 * 
 */
public final class ClassDefinitionCache
{
	public static final String RCS_ClassDefinitionCache_java = "$Id";

	private static volatile ClassDefinitionCache oThisInstance;
	private static Log oLogger = LogFactory.getLog(ClassDefinitionCache.class);
	private HashMap<String, Class<?>> oLookupTable;
	private ClassLoader oAdditionalClassLoader;

	private ClassDefinitionCache()
	{
		initInstance();
	}

	private synchronized void initInstance()
	{
		// Just in case (this could happen with multiprocessors) if we already
		// up return
		if (ClassDefinitionCache.oThisInstance != null)
			return;
		// again just in case (this could happen with multiprocessors) if we
		// already up do nothing
		if (oLookupTable == null)
			oLookupTable = new HashMap<>(10, (float) 0.75);
	}

	public static ClassDefinitionCache getInstance()
	{
		if (ClassDefinitionCache.oThisInstance == null)
			ClassDefinitionCache.oThisInstance = new ClassDefinitionCache();
		return (ClassDefinitionCache.oThisInstance);
	}

	public Class<?> lookupClassName(String varClassName)
	{
		if (varClassName == null || varClassName.length() <= 0)
			return (null);
		Class<?> aClassDefinition = oLookupTable.get(varClassName.trim());

		if (aClassDefinition == null)
		{
			try
			{
				ClassLoader aLoader = Thread.currentThread().getContextClassLoader();

				if (aLoader != null)
					aClassDefinition = Class.forName(varClassName, false, aLoader);
				else
					aClassDefinition = Class.forName(varClassName);
				oLookupTable.put(varClassName, aClassDefinition);
			}
			catch (ClassNotFoundException ex)
			{
				if (oAdditionalClassLoader != null)
				{
					try
					{
						aClassDefinition = Class.forName(varClassName, false, oAdditionalClassLoader);
						oLookupTable.put(varClassName, aClassDefinition);
					}
					catch (Exception e)
					{
						/*
						 * do nothing
						 */
					}
				}
				else
					ClassDefinitionCache.oLogger.trace("ClassDefinitionCache::lookupClassName : Class Not Found Exception, class name [" + varClassName + "] : " + ex.getMessage(), ex);
			}
			catch (Exception ex)
			{
				ClassDefinitionCache.oLogger.trace("ClassDefinitionCache::lookupClassName : Exception occurred while looking up class name [" + varClassName + "] : " + ex.getMessage(), ex);
			}
		}
		return (aClassDefinition);
	}

	public static Class<?> lookupClassDefinition(String varClassName)
	{
		ClassDefinitionCache anInstance = ClassDefinitionCache.getInstance();
		return (anInstance.lookupClassName(varClassName));
	}

	public void setAdditionalClassLoader(ClassLoader varAdditionalClassLoader)
	{
		oAdditionalClassLoader = varAdditionalClassLoader;
	}

}
