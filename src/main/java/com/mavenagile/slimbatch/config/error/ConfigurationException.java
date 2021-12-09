package com.mavenagile.slimbatch.config.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.utility.ExceptionHelper;

public class ConfigurationException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Log LOGGER = LogFactory.getLog(ConfigurationException.class);

	public ConfigurationException(Throwable varThrowable)
	{
		super(varThrowable);
		LOGGER.error("Configuration error : " + varThrowable.getMessage(), ExceptionHelper.getRootCause(varThrowable));
	}

	public ConfigurationException(Throwable varThrowable, String varErrorMessage)
	{
		super(varThrowable);
		LOGGER.error("Configuration error : " + varErrorMessage, ExceptionHelper.getRootCause(varThrowable));
	}

	public ConfigurationException(String varErrorMessage)
	{
		super(varErrorMessage);
		LOGGER.error("Configuration error : " + varErrorMessage);
	}
}
