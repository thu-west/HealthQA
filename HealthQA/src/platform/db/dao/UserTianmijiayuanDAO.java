package platform.db.dao;

import java.math.BigInteger;
import platform.util.database.ValueTransfer;


public class UserTianmijiayuanDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_tianmijiayuan"; }
	
	public BigInteger user_ID;   //唯一对应user表ID
	public int level;    //等级
	public int score;    //积分
	public int experience;   //经验
	public int prestige;   //威望
	public int money;    //金钱
	public String friend;    //好友列表，以双半角分号间隔
	public String visitor;   //访客列表，以双半角分号分解
	public int follow_count;   //关注数
	public int fan_count;     //粉丝数
	public int visit_count;    //访客数
	public int post_count;   //帖子数，回答数
	public int main_post_count;    //主贴数，主题数，问题数
	public int journal_count;    //日志数
	public int album_count;    //相册数
	public int share_count;    //分享数
	
	public UserTianmijiayuanDAO()
	{
		super();
		super.extend_table = new String("user_tianmijiayuan");
		user_ID = null;
		level = -1;
		score = -1;
		experience = -1;
		prestige = -1;
		money = -1;
		friend = null;
		visitor = null;
		follow_count = -1;
		fan_count = -1;
		visit_count = -1;
		post_count = -1;
		main_post_count = -1;
		journal_count = -1;
		album_count = -1;
		share_count = -1;
	}
	
	public void setExtendInfo(int _level, int _score, int _experience, int _prestige, int _money, int _follow_count, int _fan_count, int _visit_count, int _post_count, int _main_post_count, int _journal_count, int _album_count, int _share_count){
		level = _level;
		score = _score;
		experience = _experience;
		prestige = _prestige;
		money = _money;
		follow_count = _follow_count;
		fan_count = _fan_count;
		visit_count = _visit_count;
		post_count = _post_count;
		main_post_count = _main_post_count;
		journal_count = _journal_count;
		album_count = _album_count;
		share_count = _share_count;
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
		command[1] = "insert into `user_tianmijiayuan` (" +
				"`user_ID`,`level`,`score`," +
				"`experience`,`prestige`,`money`," +
				"`friend`,`visitor`," +
				"`follow_count`,`fan_count`,`visit_count`,`post_count`," +
				"`main_post_count`,`journal_count`,`album_count`,`share_count`" + 
				") values (" +
				      ValueTransfer.SqlValueFor(getID()) +", "+ ValueTransfer.SqlValueFor(level) +", "+ ValueTransfer.SqlValueFor(score) +
				", "+ ValueTransfer.SqlValueFor(experience) + ", "+ ValueTransfer.SqlValueFor(prestige) + ", "+ ValueTransfer.SqlValueFor(money) + 
				", "+ ValueTransfer.SqlValueFor(friend) +", "+ ValueTransfer.SqlValueFor(visitor) +
				", "+ ValueTransfer.SqlValueFor(follow_count) +", "+ ValueTransfer.SqlValueFor(fan_count) +", "+ ValueTransfer.SqlValueFor(visit_count) +", "+ ValueTransfer.SqlValueFor(post_count) +
				", "+ ValueTransfer.SqlValueFor(main_post_count) +", "+ ValueTransfer.SqlValueFor(journal_count) + ", "+ ValueTransfer.SqlValueFor(album_count) +", "+ ValueTransfer.SqlValueFor(share_count) +")";
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
