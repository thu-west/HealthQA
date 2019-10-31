package platform.db.dao;

import java.math.BigInteger;
import java.sql.Date;

import platform.db.database.ISHCDBConfig;
import platform.util.database.ValueTransfer;



public class UserDAO implements DAO {
	
	@Override
	public String getTableName() { return "user"; }
	@Override
	public String getTypeNamespace() { return type_namespace.divide(ISHCDBConfig.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("500000000000000");
	public static BigInteger ID = null;   //唯一标识，参见2
	public static String root = null;   //例如糖尿病网 www.tnbz.com
	
	public String url;   //当前页面的url
	protected String extend_table;    //"本表只提供用户的基本信息，由于病人，医生，各个网站的用户资料差距很大，很难提供统一的格式定义。所以，具体的用户格式定义需public 在扩展表中定义，后面已经定义了几个样表。\n本字段的值是扩展表的名称"
	public String username;    //昵称，姓名等
	public String gender;    //F（女），M（男）
	public int age;    //年龄
	public String address;   //居住地
	public String interest;    //兴趣爱好
	public Date register;    //注册日期
	
	public UserDAO() {
		url = null;
		extend_table = null;
		username = null;
		gender = null;
		age = -1;
		address = null;
		interest = null;
		register = null;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	
	public void setUrl(String _url){
		url = _url;
	}
	
	public void setName(String _username){
		username = _username;
	}

	public void setBaseInfo(String _gender, int _age, String _address, String _interest, String _register){
		gender = _gender;
		age = _age;
		address = _address;
		interest = _interest;
		if(_register!=null){
			String[] date_temp = _register.split("-");
			String formated_date_string = String.format("%d-%02d-%02d", Integer.parseInt(date_temp[0]), Integer.parseInt(date_temp[1]), Integer.parseInt(date_temp[2]));
			register = Date.valueOf(formated_date_string);
		}
	}
	
	public static void configure( String _root, String _namespace, String _start_ID )
	{
		initRootWebsite(_root);
		initWebsiteNamespace(_namespace);
		initStartID(_start_ID);
	}
	
	private static void initRootWebsite(String _root) {
		root = _root;
	}
	
	protected static void initStartID(String _start_ID) {
		ID = website_namespace.add(type_namespace).add( new BigInteger(_start_ID) ).add(new BigInteger("-1"));
	}
	
	protected static void initWebsiteNamespace(String namespace){
		website_namespace = new BigInteger(namespace).multiply(new BigInteger("10000000000000000"));
	}

	@Override
	public String[] insert() {
		String[] command = new String[1];
		command[0] = "insert into `user` " +
				"(`ID`,`root`,`url`,`extend_table`," +
				"`username`,`gender`,`age`,`address`," +
				"`interest`,`register`)" +
				" values " +
				"( "+ ValueTransfer.SqlValueFor(getID()) +", "+ ValueTransfer.SqlValueFor(root) +", "+ ValueTransfer.SqlValueFor(url) +", "+ ValueTransfer.SqlValueFor(extend_table) +
				", "+ ValueTransfer.SqlValueFor(username) +", "+ ValueTransfer.SqlValueFor(gender) +", "+ ValueTransfer.SqlValueFor(age) +", "+ ValueTransfer.SqlValueFor(address) +
				", "+ ValueTransfer.SqlValueFor(interest) +", "+ ValueTransfer.SqlValueFor(register) +")";		
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		BigInteger namespace = new BigInteger(website_namespace).multiply(new BigInteger("10000000000000000")).add(UserDAO.type_namespace);
		BigInteger lower, higher;
		if(start_ID == null){
			lower = namespace;
		}else{
			lower = namespace.add(new BigInteger(start_ID));
		}
		if(end_ID == null){
			higher = namespace.add(new BigInteger("99999999999999"));
		}else{
			higher = namespace.add(new BigInteger(end_ID));
		}
		String[] command = new String[1];
		command[0] = new String("delete from `user` where `ID`>="+lower.toString()+" and `ID`<="+higher.toString());
		return command;
	}
	
}
