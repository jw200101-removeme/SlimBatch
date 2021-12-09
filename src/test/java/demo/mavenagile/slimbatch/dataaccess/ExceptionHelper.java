package demo.mavenagile.slimbatch.dataaccess;

public class ExceptionHelper
{
	public static Throwable getRootCause(Throwable varException)
	{
		if (varException == null)
			return (null);
		Throwable tmpException = varException;
		Throwable theCause = varException;
		while (tmpException.getCause() != null)
		{
			if (tmpException.getCause() != null)
				tmpException = theCause = tmpException.getCause();
			else
				break;
		}
		return theCause;
	}
}
