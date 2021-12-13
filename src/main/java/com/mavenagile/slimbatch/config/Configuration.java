package com.mavenagile.slimbatch.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import com.mavenagile.slimbatch.confi.elements.CBean;
import com.mavenagile.slimbatch.confi.elements.CProperties;
import com.mavenagile.slimbatch.confi.elements.Include;
import com.mavenagile.slimbatch.confi.elements.Job;
import com.mavenagile.slimbatch.confi.elements.Listener;
import com.mavenagile.slimbatch.config.error.ConfigurationException;
import com.mavenagile.slimbatch.utility.XMLUtility;

public class Configuration
{
	private static final String FILD_NODE = "batch";
	private static final String FILD_VALID = "valid";

	private DataSource oDataSource2X;
	private ArrayList<Include> oPlaceHolder;
	private ArrayList<Include> oIncludes;
	private ArrayList<Listener> oListeners;
	private HashMap<String, CBean> oBeans;
	private HashMap<String, CBean> oDataSource;
	private ArrayList<Job> oJobs;
	private boolean oValid;

	public Configuration()
	{
	}

	public boolean isValid()
	{
		return oValid;
	}

	public void setValid(boolean varValid)
	{
		oValid = varValid;
	}

	public DataSource getDataSource2X()
	{
		return oDataSource2X;
	}

	public void addJob(Job varJob)
	{
		if (oJobs == null)
			oJobs = new ArrayList<Job>();
		oJobs.add(varJob);
	}

	public ArrayList<Job> getJobs()
	{
		return oJobs;
	}

	public void setJobs(ArrayList<Job> varJobs)
	{
		oJobs = varJobs;
	}

	protected Job getJob(String varID)
	{
		if (oJobs != null)
		{
			for (Job aJob : oJobs)
			{
				if (aJob.getID().equals(varID))
					return (aJob);
			}
		}
		return (null);
	}

	public void addInclude(Include varInclude)
	{
		if (oIncludes == null)
			oIncludes = new ArrayList<Include>();
		oIncludes.add(varInclude);
	}

	public void addIncludeList(ArrayList<Include> varIncludes)
	{
		if (varIncludes != null && varIncludes.isEmpty() == false)
		{
			if (oIncludes == null)
				oIncludes = new ArrayList<Include>();
			oIncludes.addAll(varIncludes);
		}
	}

	public void setIncludes(ArrayList<Include> varIncludes)
	{
		oIncludes = varIncludes;
	}

	public ArrayList<Include> getIncludes()
	{
		return oIncludes;
	}

	public void addPlaceHolderProperties(Include varPlaceHolder)
	{
		if (oPlaceHolder == null)
			oPlaceHolder = new ArrayList<Include>();
		varPlaceHolder.setTagName("placeholderProperties");
		oPlaceHolder.add(varPlaceHolder);
	}

	public void addPlaceHolderPropertiesList(ArrayList<Include> varPlaceHolders)
	{
		if (varPlaceHolders != null && varPlaceHolders.isEmpty() == false)
		{
			if (oPlaceHolder == null)
				oPlaceHolder = new ArrayList<Include>();
			oPlaceHolder.addAll(varPlaceHolders);
		}
	}

	public ArrayList<Include> getPlaceHolder()
	{
		return oPlaceHolder;
	}

	public void setPlaceHolder(ArrayList<Include> varPlaceHolder)
	{
		oPlaceHolder = varPlaceHolder;
	}

	public ArrayList<Listener> getListeners()
	{
		return oListeners;
	}

	public void setListeners(ArrayList<Listener> varListeners)
	{
		oListeners = varListeners;
	}

	public void addListener(Listener varListener)
	{
		if (oListeners == null)
			oListeners = new ArrayList<Listener>();
		oListeners.add(varListener);
	}

	public void addListenersList(ArrayList<Listener> varListeners)
	{
		if (varListeners != null && varListeners.isEmpty() == false)
		{
			if (oListeners == null)
				oListeners = new ArrayList<Listener>();
			oListeners.addAll(varListeners);
		}
	}

	public void addBean(CBean varBean) throws ConfigurationException
	{
		if (oBeans == null)
			oBeans = new HashMap<String, CBean>();
		if (oBeans.get(varBean.getID()) == null)
			oBeans.put(varBean.getID(), varBean);
		else
			throw new ConfigurationException("Dublicate bean : A bean with id[" + varBean.getID() + "] has been defined as : " + oBeans.get(varBean.getID()).toXML());
	}

	public void addBeanList(HashMap<String, CBean> varBeans) throws ConfigurationException
	{
		if (varBeans != null)
		{
			if (oBeans == null)
				oBeans = new HashMap<String, CBean>();
			for (String aBeanID : varBeans.keySet())
			{
				addBean(varBeans.get(aBeanID));
			}
		}
	}

	public HashMap<String, CBean> getBeans()
	{
		return oBeans;
	}

	public Object getBean(String varBeanID, CProperties varCustomProperties) throws ConfigurationException
	{
		CBean aBean = oBeans.get(varBeanID);
		if (aBean != null)
		{
			try
			{
				return (aBean.getBeanObject(varCustomProperties));
			}
			catch (Exception e)
			{
				throw new ConfigurationException(e, "Can not instantiate Bean with id[" + varBeanID + "] : " + e.getMessage() + "\n	bean has been defined as : " + aBean.toXML());
			}
		}
		return (null);
	}

	public void addDataSource(CBean varDataSource) throws ConfigurationException
	{
		if (oDataSource == null)
			oDataSource = new HashMap<String, CBean>();
		if (oDataSource.get(varDataSource.getID()) == null)
		{
			varDataSource.setSingleton(true);
			varDataSource.setTagName("datasource");
			oDataSource.put(varDataSource.getID(), varDataSource);
		}
		else
			throw new ConfigurationException("Dublicate datasource : A datasource with id[" + varDataSource.getID() + "] + has been defined as : " + oDataSource.get(varDataSource.getID()).toXML());

	}

	public void addDataSourceList(HashMap<String, CBean> varBeans) throws ConfigurationException
	{
		if (varBeans != null)
		{
			if (oDataSource == null)
				oDataSource = new HashMap<String, CBean>();
			for (Entry<String, CBean> anEntry : varBeans.entrySet())
			{
				addDataSource(anEntry.getValue());
			}
		}
	}

	public HashMap<String, CBean> getDataSources()
	{
		return oDataSource;
	}

	public DataSource getDataSource(String varBeanID) throws ConfigurationException
	{
		CBean aBean = oDataSource.get(varBeanID);
		if (aBean != null)
		{
			try
			{
				return (DataSource) (aBean.getBeanObject(null));
			}
			catch (Exception e)
			{
				throw new ConfigurationException(e, "Can not instantiate DataSource with id[" + varBeanID + "] + has been defined as : " + aBean.toXML());
			}
		}
		return (null);
	}

	@Override
	public String toString()
	{
		return "Configuration [oDataSource2X=" + oDataSource2X + ", oPlaceHolder=" + oPlaceHolder + ", oIncludes=" + oIncludes + ", oListeners=" + oListeners + ", oBeans=" + oBeans + ", oDataSource=" + oDataSource + ", oJobs=" + oJobs + ']';
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
		aBuffer.append(XMLUtility.getAttribute(FILD_VALID, oValid));
		aBuffer.append(XMLUtility.TAG_END_MARK);

		int aSubIndent = varIndent + 1;
		if (oPlaceHolder != null)
		{
			for (Include aPlaceHolder : oPlaceHolder)
				aBuffer.append(aPlaceHolder.toXML(aSubIndent));
		}

		if (oIncludes != null)
		{
			for (Include anInclude : oIncludes)
				aBuffer.append(anInclude.toXML(aSubIndent));
		}
		if (oListeners != null)
		{
			for (Listener aListener : oListeners)
				aBuffer.append(aListener.toXML(aSubIndent));
		}

		if (oDataSource != null)
		{
			for (Entry<String, CBean> aBean : oDataSource.entrySet())
				aBuffer.append(aBean.getValue().toXML(aSubIndent));
		}
		if (oBeans != null)
		{
			for (Entry<String, CBean> aBean : oBeans.entrySet())
				aBuffer.append(aBean.getValue().toXML(aSubIndent));
		}
		if (oJobs != null)
		{
			for (Job aJobs : oJobs)
				aBuffer.append(aJobs.toXML(aSubIndent));
		}

		aBuffer.append(XMLUtility.getEndTag(FILD_NODE, varIndent));
		return (aBuffer.toString());
	}

}
