package platform.db.dao;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import org.json.JSONObject;

import platform.db.database.ISHCDBConfig;
import platform.util.database.ValueTransfer;



public class WeiboDAO implements DAO {
	
	@Override
	public String getTableName() { return "weibo"; }
	@Override
	public String getTypeNamespace() { return type_namespace.divide(ISHCDBConfig.type_namespace_gain).toString(); }
	
	public static BigInteger website_namespace = null;
	public static BigInteger type_namespace = new BigInteger("1000000000000000");  // 10 00000000000000
	public static BigInteger ID = null;
	public static String root = null;
	
	public String url; // 当前页面的url
	public String md5_string; // md5
	public BigInteger author; // 创建人，可能是昵称，真实姓名等等
	public Date date; // 文章创建的日期（也可能是被转载的时间）
	public Time time; // 文章创建的时间（也可能是被转载的时间）
	public String source; // 帖子自带的分类（例如百度知道会在问题上标注分类）
	public String forward_reason; // 文章题目
	public JSONObject content;
	public int like_count; // 喜欢，顶，大拇指，支持……
	public int forward_count; // 分享数，转载数……
	public int reply_count; // 下方回复/评论的个数，有些网站会标明评论的个数
	
	public WeiboDAO() {
		content = new JSONObject();
		url = null;
		md5_string = null;
		author = null;
		date = null;
		time = null;
		like_count = -1;
		reply_count = -1;
	}
	
	public void setPosition(String _url){
		url = _url;
	}
	
	public void setMD5(String _md5_string){
		md5_string = _md5_string;
	}
	
	public void setStatistic(int _like_count, int _forward_count, int _reply_count){
		like_count = _like_count;
		forward_count = _forward_count;
		reply_count = _reply_count;
	}
	
	public void setMetadata(BigInteger _author, String _date, String _time, String _source, String _forward_reason){
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
		source = _source;
		forward_reason = _forward_reason;
	}
	
	@Override
	public String[] insert() {
		String[] command = new String[1];
		command[0] = "insert into `weibo` " +
				"(`ID`,`md5_string`,`root`,`url`,`author`," +
				"`date`,`time`,`source`,`forward_reason`," +
				"`content`,`like_count`," +
				"`forward_count`,`reply_count`)" +
				" values " +
				"( "+ ValueTransfer.SqlValueFor(getID()) + ", "+ ValueTransfer.SqlValueFor(md5_string) + ", " + ValueTransfer.SqlValueFor(root) +", "+ ValueTransfer.SqlValueFor(url) +", "+ ValueTransfer.SqlValueFor(author) +", "+ ValueTransfer.SqlValueFor(date) +
				", "+ ValueTransfer.SqlValueFor(time) +", "+ ValueTransfer.SqlValueFor(source) +", "+ ValueTransfer.SqlValueFor(forward_reason) +
				", "+ ValueTransfer.SqlValueFor(content.toString()) +", "+ ValueTransfer.SqlValueFor(like_count) +
				", "+ ValueTransfer.SqlValueFor(forward_count) +", "+ ValueTransfer.SqlValueFor(reply_count) +")";
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		BigInteger namespace = new BigInteger(website_namespace).multiply(new BigInteger("10000000000000000")).add(WeiboDAO.type_namespace);
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
		command[0] = new String("delete from `"+this.getTableName()+"` where `ID`>="+lower.toString()+" and `ID`<="+higher.toString());
		return command;
	}
	
	public BigInteger getID()
	{
		ID = ID.add(new BigInteger("1"));
		return ID;
	}
	public void setAddContent(String _item, String _content){
		if ( content == null )
			content = new JSONObject();
		content.put(_item, _content);
	}
	public static void configure( String _root, String _namespace, String _start_ID )
	{
		initRootWebsite(_root);
		initWebsiteNamespace(_namespace);
		initStartID(_start_ID);
	}
	
	private static void initStartID(String _start_ID) {
		ID = website_namespace.add(type_namespace).add( new BigInteger(_start_ID) ).add(new BigInteger("-1"));
	}

	private static void initRootWebsite(String _root) {
		root = _root;
	}
	
	private static void initWebsiteNamespace(String namespace){
		website_namespace = new BigInteger(namespace).multiply(new BigInteger("10000000000000000"));
	}
}
