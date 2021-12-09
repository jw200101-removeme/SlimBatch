package com.mavenagile.slimbatch.confi.elements;

import com.mavenagile.slimbatch.config.BeanHelper;
import com.mavenagile.slimbatch.utility.XMLUtility;

public class CBean
{
	private static final String FILD_NODE = "bean";
	private static final String FILD_ID = "id";
	private static final String FILD_REF = "ref";
	private static final String FILD_CLASS = "class";
	private static final String FILD_SINGLETON = "singleton";
	private String oTagName = FILD_NODE;

	private String oRef;
	private String oID;
	private String oClass;
	private CProperties oProperties;
	private boolean oSingleton;
	private Object oObject;

	protected void setBeanObjectProperty(Object tmpObject, CProperty aProperty) throws Exception
	{
		Object tmpParmObject;
		if ("boolean".equals(aProperty.getType()))
			tmpParmObject = Boolean.parseBoolean(aProperty.getValue());
		else if ("long".equals(aProperty.getType()))
			tmpParmObject = Long.valueOf(aProperty.getValue());
		else if ("short".equals(aProperty.getType()))
			tmpParmObject = Short.valueOf(aProperty.getValue());
		else if ("int".equals(aProperty.getType()))
			tmpParmObject = Integer.valueOf(aProperty.getValue());
		else if ("float".equals(aProperty.getType()))
			tmpParmObject = Float.valueOf(aProperty.getValue());
		else if ("double".equals(aProperty.getType()))
			tmpParmObject = Double.valueOf(aProperty.getValue());
		else
			tmpParmObject = aProperty.getValue();

		BeanHelper.applySetMethod(tmpObject, aProperty.getName(), tmpParmObject);

	}

	public Object getBeanObject(CProperties varCustomProperties) throws Exception
	{
		if (oSingleton && oObject != null)
			return (oObject);
		if (oObject == null && oClass != null)
		{
			ClassLoader aLoader = Thread.currentThread().getContextClassLoader();
			Class<?> aClassDefinition;
			if (aLoader != null)
				aClassDefinition = Class.forName(oClass, false, aLoader);
			else
				aClassDefinition = Class.forName(oClass);
			Object tmpObject = aClassDefinition.newInstance();
			if (oProperties != null)
			{
				for (CProperty aProperty : oProperties)
				{
					// System.out.println("PROPR" + aProperty.toXML());
					if (aProperty.getValue() != null && aProperty.getValue().length() > 0)
					{
						if (aProperty.getValue() != null && aProperty.getValue().length() > 0)
						{
							setBeanObjectProperty(tmpObject, aProperty);
						}
						// Method aMethod = BeanHelper.getMethod(aClassDefinition, "set" + aProperty.getName(), aProperty.getType());
						// aMethod.invoke(tmpObject, aProperty.getValue());
					}
				}
			}
			// reference properties override bean properties
			if (varCustomProperties != null)
			{

				for (CProperty aProperty : varCustomProperties)
				{
					if (aProperty.getValue() != null && aProperty.getValue().length() > 0)
					{
						setBeanObjectProperty(tmpObject, aProperty);
					}
				}

			}
			if (oSingleton)
				oObject = tmpObject;
			return (tmpObject);
		}
		return (null);
	}

	public String getRef()
	{
		return oRef;
	}

	public void setRef(String varRef)
	{
		oRef = varRef;
	}

	public String getID()
	{
		return oID;
	}

	public void setID(String varID)
	{
		oID = varID;
	}

	public String getBeanClass()
	{
		return oClass;
	}

	public void setClass(String varClass)
	{
		oClass = varClass;
	}

	public CProperties getProperties()
	{
		return oProperties;
	}

	public void setProperties(CProperties varProperties)
	{
		oProperties = varProperties;
	}

	public void setTagName(String varTagName)
	{
		oTagName = varTagName;
	}

	public boolean isSingleton()
	{
		return oSingleton;
	}

	public void setSingleton(boolean varSingleton)
	{
		oSingleton = varSingleton;
	}

	@Override
	public String toString()
	{
		return "CBean [oTagName=" + oTagName + ", oRef=" + oRef + ", oID=" + oID + ", oClass=" + oClass + ", oProperties=" + oProperties + ", oSingleton=" + oSingleton + ", oObject=" + oObject + "]";
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

		aBuffer.append(XMLUtility.getBeginTag(oTagName, varIndent, true));
		aBuffer.append(XMLUtility.getAttribute(FILD_ID, oID));
		aBuffer.append(XMLUtility.getAttribute(FILD_REF, oRef));
		aBuffer.append(XMLUtility.getAttribute(FILD_CLASS, oClass));
		aBuffer.append(XMLUtility.getAttribute(FILD_SINGLETON, oSingleton));

		if (oProperties != null)
		{
			aBuffer.append(XMLUtility.TAG_END_MARK);
			aBuffer.append(oProperties.toXML(varIndent + 1));
			aBuffer.append(XMLUtility.getEndTag(oTagName, varIndent));
		}
		else
			aBuffer.append(XMLUtility.TAG_END_END_MARK);
		// try
		// {
		// System.out.println("Object : " + getBeanObject());
		// }
		// catch (Exception e)
		// {
		// System.out.println("Object class : " + oClass);
		// e.printStackTrace();
		// }
		return (aBuffer.toString());
	}
}
