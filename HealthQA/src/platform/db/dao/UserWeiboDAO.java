package platform.db.dao;

import java.math.BigInteger;
import platform.util.database.ValueTransfer;


public class UserWeiboDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_weibo"; }
	
	public BigInteger user_ID;   //唯一对应user表ID
	int level = -1;
	int post_count = -1;
	int follow_count = -1;
	int fans_count = -1;
	
	public UserWeiboDAO()
	{
		super();
		super.extend_table = new String("user_weibo");
		user_ID = null;
		level = -1;
		post_count = -1;
		follow_count = -1;
		fans_count = -1;
	}
	
	public void setExtendInfo(int _level,  int _post_count,  int _follow_count,  int _fans_count){
		level = _level;
		post_count = _post_count;
		follow_count = _follow_count;
		fans_count = _fans_count;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];
		command[1] = "insert into `user_weibo` (`user_ID`, `level`, `post_count`, `follow_count`, `fans_count`) "
				+ " values ("
				+ ValueTransfer.SqlValueFor(getID()) + ", "
				+ ValueTransfer.SqlValueFor(level) + ","
				+ ValueTransfer.SqlValueFor(post_count) + ","
				+ ValueTransfer.SqlValueFor(follow_count) + ","
				+ ValueTransfer.SqlValueFor(fans_count) + ")";
		return command;
	}

	@Override
	public String[] delete(String website_namespace, String start_ID,
			String end_ID) {
		return super.delete(website_namespace, start_ID, end_ID);
		/*
		 * 外键设置为delete cascade，所以不需要对子表进行处理，子表会自动被删除。
		 */
	}
	
	/*
	 * the following is design to overload the super's function
	 */
	@Override
	public void setUrl(String _url){
		super.setUrl(_url);
		if(user_ID == null)
			user_ID = super.getID();
	}
	
	@Override
	public BigInteger getID()
	{
		if(user_ID==null){
			user_ID = super.getID();
		}
		return user_ID;
	}
	
}
