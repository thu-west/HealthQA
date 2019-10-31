package platform.db.database;

import java.math.BigInteger;

public class FullID {
	public String website_namespace;
	public String type_namespace;
	public String relative_ID;
	BigInteger absolute_ID;
	BigInteger namespace;
	
	public FullID(){
		
	}
	public FullID(String _website_namespace, String _type_namespace, String _relative_ID){
		website_namespace = _website_namespace;
		type_namespace = _type_namespace;
		relative_ID = _relative_ID;
		namespace = (new BigInteger(website_namespace).multiply(ISHCDBConfig.website_namespace_gain))
				.add(new BigInteger(type_namespace).multiply(ISHCDBConfig.type_namespace_gain));
		absolute_ID = namespace.add(new BigInteger(relative_ID));
	}
	
	public BigInteger getAbsoluteID(){
		return absolute_ID;
	}
	
	public static FullID parseAbsoluteID(BigInteger absolute_ID){
		FullID id = new FullID();
		BigInteger[] temp = absolute_ID.divideAndRemainder(ISHCDBConfig.website_namespace_gain);
		id.website_namespace = temp[0].toString();
		BigInteger[] temp1 = temp[1].divideAndRemainder(ISHCDBConfig.type_namespace_gain);
		id.type_namespace = temp1[0].toString();
		id.relative_ID = temp1[1].toString();
		id.namespace = absolute_ID.subtract(temp1[1]);
		return id;
	}
	
	public static void main(String[] args) {
		FullID id = new FullID("4","1","0");
		System.out.println(id.absolute_ID);
	}
	
}
