package platform.db.dao;

import java.math.BigInteger;
import platform.util.database.ValueTransfer;


public class UserTianyaDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_tianya"; }
	
	public BigInteger user_ID;   //唯一对应user表ID
	public int score;    //积分
	public int experience;   //经验
	public int fans_count;     //粉丝数
	public int times;    //登录次数
	public String friend;    //好友列表，以双半角分号间隔
	public String visitor;   //访客列表，以双半角分号分解
	
	public UserTianyaDAO()
	{
		super();
		super.extend_table = new String("user_tianya");
		user_ID = null;
		score = 0;
		experience = -1;
		friend = null;
		visitor = null;
		fans_count = -1;
		times = -1;
	}
	
	public void setExtendInfo(int _score, int _experience, int _fans_count, int _times){
		score = _score;
		experience = _experience;
		fans_count = _fans_count;
		times = _times;
	}
	
	public void setFriends(String _friend){
		friend = _friend;
	}
	
	public void setVisitors(String _visitor){
		visitor = _visitor;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];
		command[1] = "insert into `user_tianya` (" +
				"`user_ID`,`score`," +
				"`experience`," +
				"`friend`,`visitor`," +
				"`fans_count`,`times`" + 
				") values (" +
				      ValueTransfer.SqlValueFor(getID()) +", "+ ValueTransfer.SqlValueFor(score) +
				", "+ ValueTransfer.SqlValueFor(experience) + 
				", "+ ValueTransfer.SqlValueFor(friend) +", "+ ValueTransfer.SqlValueFor(visitor) +
				", "+ ValueTransfer.SqlValueFor(fans_count) + ", "+ ValueTransfer.SqlValueFor(times) + ")";
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
