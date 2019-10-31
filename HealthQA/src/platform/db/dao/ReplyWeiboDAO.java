package platform.db.dao;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import org.json.JSONObject;

import platform.db.database.ISHCDBConfig;
import platform.util.database.ValueTransfer;



public class ReplyWeiboDAO implements DAO {
	@Override
	public String getTableName() { return "reply_weibo"; }
	@Override
	public String getTypeNamespace() { return type_namespace.divide(ISHCDBConfig.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("1100000000000000");
	public static BigInteger ID = null;
	
	public BigInteger reply_ID;    //标识其回复的article
	public BigInteger nested_reply_ID;   //回复的回复
	public String url;
	public BigInteger author;    //作者昵称或者姓名
	public Date date;    //文章创建的日期（也可能是被转载的时间）
	public Time time;    //文章创建的时间（也可能是被转载的时间）
	public JSONObject content;
	public String category;   //'评论或转发理由'
	public String source;

	public ReplyWeiboDAO() {
		reply_ID = null;
		nested_reply_ID = null;
		author = null;
		date = null;
		time = null;
		content = new JSONObject();
		category = null;
		source = null;
		url = null;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	
	public void setReplyObject(BigInteger _reply_ID, BigInteger _nested_reply_ID){
		reply_ID = _reply_ID;
		nested_reply_ID = _nested_reply_ID;
	}
	
	public void setUrl(String _url){
		url = _url;
	}

	public void setMetadata(BigInteger _author, String _date, String _time, String _category, String _source){
		if(_date != null){
			String[] date_temp = _date.split("-");
			String formated_date_string = String.format("%d-%02d-%02d", Integer.parseInt(date_temp[0]), Integer.parseInt(date_temp[1]), Integer.parseInt(date_temp[2]));
			date = Date.valueOf(formated_date_string);
		}
		if(_time != null){
			String[] time_temp = _time.split(":");
			String formated_time_string = String.format("%02d:%02d:%02d", Integer.parseInt(time_temp[0]), Integer.parseInt(time_temp[1]), Integer.parseInt(time_temp[2]));
			time = Time.valueOf(formated_time_string);
		}
		author = _author;
		category = _category;
		source = _source;
	}
	
	public void setAddContent(String _item, String _content){
		if ( content == null )
			content = new JSONObject();
		content.put(_item, _content);
	}
	
	public static void configure( String _namespace, String _start_ID )
	{
		initWebsiteNamespace(_namespace);
		initStartID(_start_ID);
	}
	
	private static void initStartID(String _start_ID) {
		ID = website_namespace.add(type_namespace).add( new BigInteger(_start_ID) ).add(new BigInteger("-1"));
	}
	
	private static void initWebsiteNamespace(String namespace){
		website_namespace = new BigInteger(namespace).multiply(new BigInteger("10000000000000000"));
	}
	
	@Override
	public String[] insert() {
		String[] command = new String[1];
		command[0] = "insert into `reply_weibo` "
				+ "(`ID`,`reply_ID`,`nested_reply_ID`,`url`,"
				+ "`author`,`date`,`time`, `category`, `source`,"
				+ "`content`)" + " values " + "( "
				+ ValueTransfer.SqlValueFor(getID()) + ", " + ValueTransfer.SqlValueFor(reply_ID) + ", " + ValueTransfer.SqlValueFor(nested_reply_ID) + ","
				+ ValueTransfer.SqlValueFor(url) + ", " + ValueTransfer.SqlValueFor(author) + ", "
				+ ValueTransfer.SqlValueFor(date) + ", " + ValueTransfer.SqlValueFor(time) + ", " + ValueTransfer.SqlValueFor(category) + ","
				+ ValueTransfer.SqlValueFor(source) + "," + ValueTransfer.SqlValueFor(content.toString())
				+ ")";
				
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		BigInteger namespace = new BigInteger(website_namespace).multiply(
				new BigInteger("10000000000000000")).add(
				ReplyArticleDAO.type_namespace);
		BigInteger lower, higher;
		if (start_ID == null) {
			lower = namespace;
		} else {
			lower = namespace.add(new BigInteger(start_ID));
		}
		if (end_ID == null) {
			higher = namespace.add(new BigInteger("99999999999999"));
		} else {
			higher = namespace.add(new BigInteger(end_ID));
		}
		String[] command = new String[1];
		command[0] = new String("delete from `reply_article` where `ID`>="
				+ lower.toString() + " and `ID`<=" + higher.toString());
		return command;
	}
}
