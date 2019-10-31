package platform.db.dao;

import java.math.BigInteger;
import platform.util.database.ValueTransfer;


public class UserYoukuDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_youku"; }
	
	public BigInteger user_ID;   //唯一对应user表ID
	public int fans_count;     //粉丝数
	public int visit_count;    //访客数
	public int video_play_count; //播放数
	public int subscribe_count; //订阅数
	public int video_count;  //视频数
	public int album_count;    //专辑数
	public String following;    //好友列表，以双半角分号间隔
	public String follower;   //访客列表，以双半角分号分解
	
	public UserYoukuDAO()
	{
		super();
		super.extend_table = new String("user_youku");
		user_ID = null;
		following = null;
		follower = null;
		fans_count = -1;
		visit_count = -1;
		video_play_count = -1;
		video_count = -1;
		subscribe_count = -1;
		album_count = -1;
	}
	
	public void setExtendInfo(String _following, String _follower, int _fans_count, int _visit_count, int _video_play_count, int _video_count, int _subscribe_count, int _album_count){
		following = _following;
		follower = _follower;
		fans_count = _fans_count;
		visit_count = _visit_count;
		video_play_count = _video_play_count;
		video_count = _video_count;
		subscribe_count = _subscribe_count;
		album_count = _album_count;
	}
	
	public void setFollowing(String _following){
		following = _following;
	}
	
	public void setFollower(String _follower){
		follower = _follower;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];
		command[1] = "insert into `user_youku` (`user_ID`, `following`,`follower`,`fans_count`,`visit_count`,`video_play_count`,`video_count`,`subscribe_count`,`album_count`) "
				+ " values ("
				+ ValueTransfer.SqlValueFor(getID())
				+ ", "
				+ ValueTransfer.SqlValueFor(following)
				+ ","
				+ ValueTransfer.SqlValueFor(follower)
				+ ","
				+ ValueTransfer.SqlValueFor(fans_count)
				+ ","
				+ ValueTransfer.SqlValueFor(visit_count)
				+ ","
				+ ValueTransfer.SqlValueFor(video_play_count)
				+ ","
				+ ValueTransfer.SqlValueFor(video_count)
				+ ","
				+ ValueTransfer.SqlValueFor(subscribe_count)
				+ ","
				+ ValueTransfer.SqlValueFor(album_count) + ")";
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
