package com.mavenagile.slimbatch.utility;
/*
 * Created on Sep 10, 2005
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 
 */
public class XMLMapper
{
	public static final String CONSTRING_NAME = "name";

	public static final String ATTRIBUTE_NAME_ELEMENT_CLASS = "elementClass";

	protected String oClassNameAttribute = XMLMapper.ATTRIBUTE_NAME_ELEMENT_CLASS;
	private static Log oLogger = LogFactory.getLog(XMLMapper.class);
	/**
	 * This a workaround not having a schema parser on Android
	 */
	private Map<String, String> oClassLookupTable;

	protected static final ClassDefinitionCache oClassDefinitionCache = ClassDefinitionCache.getInstance();

	protected Method findMethod(Object varObject, String varMethod, Object varParameterType)
	{
		if (XMLMapper.oLogger.isTraceEnabled())
			XMLMapper.oLogger.trace("	Finding a method named [" + varMethod + "] in class [" + varObject.getClass() + "] with parm[" + (varParameterType != null ? varParameterType.getClass().getName() : "none") + "] ");
		if (XMLUtility.isNull(varMethod))
			return (null);
		Method[] aMethods = varObject.getClass().getMethods();

		for (int i = aMethods.length - 1; i >= 0; i--)
		{
			if (aMethods[i].getName().equalsIgnoreCase(varMethod) //
					&& ((varParameterType == null) //
							|| (aMethods[i].getParameterTypes() != null //
									&& aMethods[i].getParameterTypes().length == 1 //
									&& aMethods[i].getParameterTypes()[0].isAssignableFrom(varParameterType.getClass()))))
			{
				return (aMethods[i]);
			}
		}
		return (null);
	}

	protected Object getObject(String varClassName, String varArgument) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Class<?> aClassDef = XMLMapper.oClassDefinitionCache.lookupClassName(varClassName);
		if (XMLMapper.oLogger.isTraceEnabled())
			XMLMapper.oLogger.trace("	Looking to create an object of type [" + varClassName + "] argument [" + varArgument + "], found class [" + aClassDef + ']');
		if (aClassDef == null)
			return (null);
		if (varArgument == null || varArgument.length() <= 0)
			return (aClassDef.newInstance());
		return (aClassDef.getConstructor(String.class).newInstance(varArgument));
	}

	/**
	 * Order of method search : setTagName(with exact type) setTagName(String) addTagName add
	 *
	 * @param varObject
	 * @param varMethodName
	 * @param varValue
	 * @throws XMLMapperException
	 *             void
	 *
	 */
	protected void applySetMethod(Object varObject, String varMethodName, Object varValue) throws XMLMapperException
	{
		Method aMethod = findMethod(varObject, JavaClassHelper.makeMethodName(varMethodName, "set"), varValue);
		if (aMethod == null)
			// if setMethod not found for varValue type, find the first setMethod with any parameter type
			aMethod = findMethod(varObject, JavaClassHelper.makeMethodName(varMethodName, "set"), null);
		if (aMethod == null)
			aMethod = findMethod(varObject, "add" + varMethodName, varValue);
		if (aMethod == null)
			aMethod = findMethod(varObject, "add", varValue);
		if (aMethod == null)
			aMethod = findMethod(varObject, "add", null);
		if (aMethod == null)
			throw (new XMLMapperException("Undefined method set" + varMethodName + ", add" + varMethodName + " or add" + " for class " + varObject.getClass().getName() + " value type : " + varValue.getClass()));
		try
		{
			Class<?> parameterType = aMethod.getParameterTypes()[0];

			if (XMLMapper.oLogger.isTraceEnabled())
				XMLMapper.oLogger.trace("	Invoke method [" + aMethod.getName() + "] : parm[" + parameterType + "] value [" + varValue + "] on class [" + varObject.getClass() + ']');

			boolean isValueString = varValue.getClass() == String.class;
			if (parameterType == String.class)
				aMethod.invoke(varObject, varValue);
			else if (isValueString)
			{
				if (parameterType == boolean.class)
					aMethod.invoke(varObject, Boolean.valueOf((String) varValue));
				else if (parameterType == long.class)
					aMethod.invoke(varObject, Long.valueOf((String) varValue));
				else if (parameterType == short.class)
					aMethod.invoke(varObject, Short.valueOf((String) varValue));
				else if (parameterType == int.class)
					aMethod.invoke(varObject, Integer.valueOf((String) varValue));
				else if (parameterType == float.class)
					aMethod.invoke(varObject, Float.valueOf((String) varValue));
				else if (parameterType == double.class)
					aMethod.invoke(varObject, Double.valueOf((String) varValue));
				else
					aMethod.invoke(varObject, varValue);
			}
			else
				aMethod.invoke(varObject, varValue);
		}
		catch (Exception e)
		{
			XMLMapper.oLogger.debug("Invalid method : Class [" + varObject.getClass().getName() + "] method [" + aMethod.getName() + //
					"] parameterType [" + (aMethod.getParameterTypes()[0].getName()) + //
					"] argument type [" + varValue.getClass().getName() + "] argument object : " + varValue, e);
			throw (new XMLMapperException(
					"Invalid method : Class [" + varObject.getClass().getName() + "] method [" + aMethod.getName() + "] parameterType [" + aMethod.getParameterTypes()[0].getName() + "] value type [" + varValue.getClass().getName() + ']', e));
		}
	}

	/**
	 * If an element has a "name" attribute, it will be used to instantiate the object if an element has a text body, it will be used to instantiate the object if an element has a
	 * "name" attribute and a text body, "name" will be used to instantiate the object, and a setText method will be excuted on the resulting object.
	 *
	 * @param varNode
	 * @param varResult
	 * @param varTab
	 * @return
	 * @throws XMLMapperException
	 *             Object
	 *
	 */
	public Object processNode(Node varNode, Object varResult) throws XMLMapperException
	{
		if (varNode.getNodeType() == Node.COMMENT_NODE)
			return (varResult);
		boolean readNameAttribute = false;
		String aNodeName = varNode.getNodeName();
		String aNodeValue = null;
		String aClassType = null;
		Object currentObj = null;

		if (varNode.getAttributes().getNamedItem(oClassNameAttribute) != null)
			aClassType = varNode.getAttributes().getNamedItem(oClassNameAttribute).getNodeValue();
		if (aClassType == null && oClassLookupTable != null)
			aClassType = oClassLookupTable.get(aNodeName);
		if (varNode.getFirstChild() != null)
			aNodeValue = varNode.getFirstChild().getNodeValue().trim();
		if (aClassType != null)
		{
			try
			{
				if (varNode.getAttributes().getNamedItem(CONSTRING_NAME) != null)
				{
					readNameAttribute = true;
					currentObj = getObject(aClassType, varNode.getAttributes().getNamedItem(CONSTRING_NAME).getNodeValue());
					if (!XMLUtility.isNull(aNodeValue))
						applySetMethod(currentObj, "Text", aNodeValue);
				}
				else
					currentObj = getObject(aClassType, aNodeValue);
			}
			catch (Exception e)
			{
				XMLMapper.oLogger.debug("Error while creating an object of type [" + aClassType + "] : node name[" + aNodeName + "] constructor argument(" + //
						(varNode.getAttributes().getNamedItem(CONSTRING_NAME) != null ? varNode.getAttributes().getNamedItem(CONSTRING_NAME).getNodeValue() : aNodeValue) + ") : " + varResult);
				throw (new XMLMapperException("Can not create object of type [" + aClassType + "] :  name[" + aNodeName + "] constructor (" + //
						(varNode.getAttributes().getNamedItem(CONSTRING_NAME) != null ? varNode.getAttributes().getNamedItem(CONSTRING_NAME).getNodeValue() : aNodeValue) + ") : " + varResult, e));
			}
		}
		else
			throw (new XMLMapperException("Element has no defined Java class : " + aNodeName));

		if (XMLMapper.oLogger.isTraceEnabled())
			XMLMapper.oLogger.trace("Creating an object of type [" + aClassType + "] : node name[" + aNodeName + "] constructor argument("
					+ (varNode.getAttributes().getNamedItem(CONSTRING_NAME) != null ? varNode.getAttributes().getNamedItem(CONSTRING_NAME).getNodeValue() : aNodeValue) + ") ");

		if (currentObj == null)
			throw (new XMLMapperException("Undefined call to object : " + aClassType + " while reading " + aNodeName + " node value: " + aNodeValue));
		if (varResult == null)
			varResult = currentObj;

		if (varNode.getAttributes() != null)
		{
			for (int i = 0; i < varNode.getAttributes().getLength(); i++)
			{
				if (varNode.getAttributes().item(i).getNodeName().indexOf(':') == -1 && !varNode.getAttributes().item(i).getNodeName().equals(oClassNameAttribute)
						&& (!CONSTRING_NAME.equals(varNode.getAttributes().item(i).getNodeName()) || !readNameAttribute))
				{
					if (XMLMapper.oLogger.isTraceEnabled())
						XMLMapper.oLogger.trace(" --- : set attribute " + aNodeName + ".set" + varNode.getAttributes().item(i).getNodeName() + " : to value[" + varNode.getAttributes().item(i).getNodeValue() + ']');
					applySetMethod(currentObj, varNode.getAttributes().item(i).getNodeName(), varNode.getAttributes().item(i).getNodeValue());
				}
			}
		}
		for (int i = 0; i < varNode.getChildNodes().getLength(); i++)
		{
			if (varNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
				processNode(varNode.getChildNodes().item(i), currentObj);
		}
		if (varResult != currentObj)
		{
			if (XMLMapper.oLogger.isTraceEnabled())
				XMLMapper.oLogger.trace(" --- :  Add object of type [" + currentObj.getClass() + "] to [" + varResult.getClass() + "] for node [" + aNodeName + ']');
			applySetMethod(varResult, aNodeName, currentObj);
		}
		return (varResult);
	}

	/**
	 * @param varClassNameAttribute
	 *            the classNameAttribute to set
	 */
	public void setClassNameAttribute(String varClassNameAttribute)
	{
		oClassNameAttribute = varClassNameAttribute;
	}

	public void setClassLookupTable(Map<String, String> varClassLookupTable)
	{
		oClassLookupTable = varClassLookupTable;
	}

}
