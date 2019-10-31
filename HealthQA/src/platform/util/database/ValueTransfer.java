package platform.util.database;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;

public class ValueTransfer {
	public static String SqlValueFor( String a ){
		if( a == null )
			return "null";
		else
		{
			return "\""+EscapeProcessor.escape(a)+"\"";
		}
	}
	
	public static String SqlValueFor( int a ){
		return Integer.toString(a);
	}
	
	public static String SqlValueFor( float a ){
		return Float.toString(a);
	}
	
	public static String SqlValueFor( Date a ){
		if( a == null )
			return "null";
		else
			return "\""+a.toString()+"\"";
	}
	
	public static String SqlValueFor( Time a ){
		if( a == null )
			return "null";
		else
			return "\""+a.toString()+"\"";
	}
	
	public static String SqlValueFor( BigInteger a ){
		if( a == null )
			return "null";
		else
			return a.toString();
	}
}