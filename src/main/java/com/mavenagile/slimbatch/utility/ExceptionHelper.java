package com.mavenagile.slimbatch.utility;

public final class ExceptionHelper
{
	private ExceptionHelper()
	{
	}

	public static Throwable getRootCause(Throwable varException)
	{
		if (varException == null)
			return (null);
		Throwable tmpException = varException;
		Throwable theCause = null;
		while (tmpException != null)
		{
			theCause = tmpException;
			tmpException = tmpException.getCause();
		}
		return theCause;
	}
}
