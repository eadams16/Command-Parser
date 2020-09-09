package sbw.project.cli.parser;

public class ArgumentValidator {
	public final static boolean isValidAcceleration(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidAcceleration for param: " + str);
		}
		return checkNonNegReal(str);
	}
	public final static boolean isValidAngle(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidAngle for param: " + str);
		}
		if(checkNonNegReal(str)) {
			return true;
		}
		return false;
	}
	public final static boolean isValidIdentifier(String str)
	{
		return javax.lang.model.SourceVersion.isIdentifier(str);
	}
	public final static boolean isValidPercent(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidPercent for param: " + str);
		}
		if(str.charAt(0) == '.')
		{
			return false;
		}
		double d = -1;
		try
		{
			d = Double.parseDouble(str);
		}
		catch(Exception e)
		{
			return false;
		}
		if(d < 0 || d > 100)
		{
			return false;
		}
		return true;
	}
	public final static boolean isValidPosition(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidPercent for param: " + str);
		}
		if	   (str == "1" ||
				str == "2" ||
				str == "3" ||
				str == "4" ||
				str == "UP")
		{
			return true;
		}
		return false;
	}
	public final static boolean isValidPower(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidPower for param: " + str);
		}
		return isValidPercent(str);
	}
	public final static boolean isValidRate(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidRate for param: " + str);
		}
		return checkNonNegReal(str);
	}
	public final static boolean isValidSpeed(String str)
	{
		if(str == null || str.isEmpty())
		{
			throw new RuntimeException("Bad params str method isValidSpeed for param: " + str);
		}
		return checkNonNegReal(str);
	}
	private final static boolean checkNonNegReal(String str)
	{
		if(str.charAt(0) == '.')
		{
			return false;
		}
		double d = -1;
		try
		{
			d = Double.parseDouble(str);
		}
		catch(Exception e)
		{
			return false;
		}
		if(d < 0)
		{
			return false;
		}
		return true;
	}
	
}
