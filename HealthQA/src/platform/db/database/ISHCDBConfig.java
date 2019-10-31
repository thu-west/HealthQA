package platform.db.database;

import java.math.BigInteger;
import java.util.HashMap;

import platform.util.database.DBConfig;


public class ISHCDBConfig extends DBConfig {
	
	public ISHCDBConfig(String url, String username, String password){
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public ISHCDBConfig() {
		url = new String( "" );
		username = new String( "root" );
		password = new String("123qwe");
	}
	
	/*
	 * global parameter
	 */
	public static int sql_cache_size = 1;
	
	
	/*
	 * Rule Definition Of ID
	 */
	public static final BigInteger website_namespace_gain = new BigInteger("10000000000000000");
	public static final BigInteger type_namespace_gain = new BigInteger("100000000000000");
	public static HashMap<String, String> TYPE_NS;
	public static HashMap<String, String> WEBSITE_NS;
	
	static {
		TYPE_NS = new HashMap<String, String>();
		TYPE_NS.put("article", "00");
		TYPE_NS.put("post", "01");
		TYPE_NS.put("reply_article", "02");
		TYPE_NS.put("reply_post", "03");
		TYPE_NS.put("video", "04");
		TYPE_NS.put("user", "05");
		
		WEBSITE_NS = new HashMap<String, String>();
		WEBSITE_NS.put("zhidao.baidu.com", "001");   // managed by Fire
		WEBSITE_NS.put("wenwen.soso.com", "002");    // managed by Fire
		WEBSITE_NS.put("www.youku.com", "003");      // managed by Fire
		WEBSITE_NS.put("ask.39.net", "004");	  	 // managed by Fire
		WEBSITE_NS.put("club.xywy.com", "005");	  	 // managed by Fire
		WEBSITE_NS.put("iqiyi.com", "006");	  	 // managed by Fire
		WEBSITE_NS.put("wenyi.baidu.com", "007");	  	 // managed by Fire
		WEBSITE_NS.put("120ask.com", "008");	  	 // managed by Fire
		WEBSITE_NS.put("�������ư�", "100");   // managed by cxh
		WEBSITE_NS.put("����������", "101");    // managed by cxh
		WEBSITE_NS.put("������ʳ��", "102");   // managed by cxh
		WEBSITE_NS.put("����a��", "103");    // managed by cxh
		WEBSITE_NS.put("����ҽԺ", "104");    // managed by cxh
		WEBSITE_NS.put("weibo.cn", "105");    // managed by cxh
		WEBSITE_NS.put("tianmijiayuan", "106");    // managed by cxh
	}
}
