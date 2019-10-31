package platform.db.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;


public class UserBaidutiebaDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_baidutieba"; }
	
	public BigInteger user_ID;   //唯一对应user表ID
	public int like_bars_count;   //喜欢的吧数
	public int fans_count;     //吧粉丝数
	public int zhidao_reply;    //知道回答数
	public float zhidao_accepted;  //知道采纳率
	public int zhidao_grade;    //知道身份等级
	public int space_arcticle_count;    //空间文章
	public int space_fans_count;   //空间粉丝
	
	public UserBaidutiebaDAO()
	{
		super();
		super.extend_table = new String("user_baidutieba");
		user_ID = null;
		like_bars_count = -1;
		fans_count = -1;
		zhidao_reply = -1;
		zhidao_accepted = -1;
		zhidao_grade = -1;
		space_arcticle_count = -1;
		space_fans_count = -1;
	}
	
	public void setExtendInfo(int _like_bars_count, int _fans_count, int _zhidao_reply, float _zhidao_accepted, int _zhidao_grade, int _space_arcticle_count, int _space_fans_count){
		like_bars_count = _like_bars_count;
		fans_count = _fans_count;
		zhidao_reply = _zhidao_reply;
		zhidao_accepted = _zhidao_accepted;
		zhidao_grade = _zhidao_grade;
		space_arcticle_count = _space_arcticle_count;
		space_fans_count = _space_fans_count;
	}
	
	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];
		
		command[1] = "insert into `user_baidutieba` (" +
				"`user_ID`, `like_bars_count`, `fans_count`, " +
				"`zhidao_reply`, `zhidao_accepted`, `zhidao_grade`, " +
				"`space_arcticle_count`, `space_fans_count`" + 
				") values (" +
				      ValueTransfer.SqlValueFor(getID()) +", "+ ValueTransfer.SqlValueFor(like_bars_count) + ", "+ ValueTransfer.SqlValueFor(fans_count) + 
				", "+ ValueTransfer.SqlValueFor(zhidao_reply) +", "+ ValueTransfer.SqlValueFor(zhidao_accepted) + ", "+ ValueTransfer.SqlValueFor(zhidao_grade) + 
				", "+ ValueTransfer.SqlValueFor(space_arcticle_count) + "," + ValueTransfer.SqlValueFor(space_fans_count) + ")";
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
