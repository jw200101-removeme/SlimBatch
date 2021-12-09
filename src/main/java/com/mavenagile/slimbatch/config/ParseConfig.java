package com.mavenagile.slimbatch.config;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mavenagile.slimbatch.confi.elements.Include;
import com.mavenagile.slimbatch.confi.elements.Job;
import com.mavenagile.slimbatch.config.error.ConfigurationException;
import com.mavenagile.slimbatch.utility.ExceptionHelper;
import com.mavenagile.slimbatch.utility.XMLMapper;

// TODO: Recovered from 7/8/2017 11:18
public class ParseConfig
{
	private static Log LOGGER = LogFactory.getLog(ParseConfig.class);

	private Configuration oConfiguration;
	private DocumentBuilder oParser;

	public ParseConfig()
	{
	}

	private DocumentBuilder getDocumentBuilder()
	{
		if (oParser == null)
		{
			try
			{
				SchemaFactory aSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema aSlimBatchSchema = aSchemaFactory.newSchema(ParseConfig.class.getResource("slim-batch.xsd"));

				DocumentBuilderFactory aDocumentFactory = DocumentBuilderFactory.newInstance();
				aDocumentFactory.setSchema(aSlimBatchSchema);
				aDocumentFactory.setNamespaceAware(true);
				aDocumentFactory.setExpandEntityReferences(true);
				aDocumentFactory.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
				aDocumentFactory.setFeature("http://xml.org/sax/features/validation", true);
				aDocumentFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
				aDocumentFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
				aDocumentFactory.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);

				oParser = aDocumentFactory.newDocumentBuilder();

				oParser.setEntityResolver(new EntityResolver()
					{
						public InputSource resolveEntity(String varPublicId, String varSystemId) throws SAXException, IOException
						{
							if (varSystemId.endsWith("slim-batch.xsd"))
								return new InputSource(ParseConfig.class.getResourceAsStream("slim-batch.xsd"));
							else
								return null;
						}
					});
				oParser.setErrorHandler(new ErrorHandler()
					{

						@Override
						public void warning(SAXParseException varException) throws SAXException
						{
							LOGGER.error("Configuration warning : " + varException.getMessage(), ExceptionHelper.getRootCause(varException));
						}

						@Override
						public void fatalError(SAXParseException varException) throws SAXException
						{
							LOGGER.error("Configuration fatalError : " + varException.getMessage(), ExceptionHelper.getRootCause(varException));
							throw varException;
						}

						@Override
						public void error(SAXParseException varException) throws SAXException
						{
							// LOGGER.error("Configuration error : " + varException.getMessage(), ExceptionHelper.getRootCause(varException));
							throw varException;
						}
					});
			}
			catch (Exception aE)
			{
				LOGGER.error("Configuration error : " + aE.getMessage(), ExceptionHelper.getRootCause(aE));
				oParser = null;
			}
		}
		return (oParser);
	}

	private Document getDocument(String varResourcePath) throws Exception
	{
		// if (LOGGER.isTraceEnabled())
		// LOGGER.trace("getDocument for " + varResourcePath + " from path : " + getClass().getClassLoader().getResource(""));
		try
		{
			getDocumentBuilder();
			Document aDocument = oParser.parse(ParseConfig.class.getClassLoader().getResourceAsStream(varResourcePath));
			aDocument.normalize();
			return (aDocument);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public Configuration getConfiguration() throws Exception
	{
		return (getConfiguration("META-INF/slim-batch.xml"));
	}

	private Configuration getConfiguration(String varResourcePath) throws ConfigurationException, Exception
	{
		XMLMapper aXMLMapper = new XMLMapper();
		aXMLMapper.setClassNameAttribute("nodeClass");
		Object tmpObject;
		try
		{
			Document aDocument = getDocument(varResourcePath);
			if (aDocument.getAttributes() == null)
				tmpObject = aXMLMapper.processNode(aDocument.getFirstChild(), null);
			else
				tmpObject = aXMLMapper.processNode(aDocument, null);
			if (tmpObject instanceof Configuration)
			{
				if (oConfiguration == null)
					oConfiguration = (Configuration) tmpObject;
				else
				{
					oConfiguration.addBeanList(((Configuration) tmpObject).getBeans());
					oConfiguration.addDataSourceList(((Configuration) tmpObject).getDataSources());

					oConfiguration.addPlaceHolderPropertiesList(((Configuration) tmpObject).getPlaceHolder());
					oConfiguration.addIncludeList(((Configuration) tmpObject).getIncludes());
				}
			}
			else if (tmpObject instanceof Job)
			{
				if (oConfiguration == null)
					oConfiguration = new Configuration();
				oConfiguration.addJob((Job) tmpObject);
			}

			if (oConfiguration.getIncludes() != null)
			{
				for (Include anInclude : oConfiguration.getIncludes())
				{
					if (anInclude.isFetched() == false)
					{
						anInclude.setFetched(true);
						getConfiguration(anInclude.getResource());
					}
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ConfigurationException == false)
				throw new ConfigurationException("Invalid Resource Path [" + varResourcePath + "] : " + ExceptionHelper.getRootCause(e).getMessage());
			else
				throw e;
		}
		return (oConfiguration);
	}
}
