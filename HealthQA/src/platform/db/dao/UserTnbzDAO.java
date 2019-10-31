package platform.db.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;


public class UserTnbzDAO extends UserDAO {

	@Override
	public String getTableName() {
		return "user_tnbz";
	}

	public BigInteger user_ID; // 唯一对应user表ID
	public int post_count;
	public int topic_count;
	public int integral;
	public int money;
	public int authority;
	public int experience;
	public int friend_count;
	public String treat_solution;
	public String member_type;

	public UserTnbzDAO() {
		super();
		super.extend_table = new String("user_tnbz");
		user_ID = null;
		post_count = -1;
		topic_count = -1;
		integral = -1;
		money = -1;
		authority = -1;
		experience = -1;
		friend_count = -1;
		treat_solution = null;
		member_type = null;
	}

	public void setInfo(int _post_count, int _topic_count, int _integral,
			int _money, int _authority, int _experience, int _friend_count,
			String _treat_solution, String _member_type) {
		post_count = _post_count;
		topic_count = _topic_count;
		integral = _integral;
		money = _money;
		authority = _authority;
		experience = _experience;
		friend_count = _friend_count;
		treat_solution = _treat_solution;
		member_type = _member_type;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];

		command[1] = "insert into `user_tnbz` (`user_ID`, `post_count`, `topic_count`, `integral`, `money`, `authority`, `experience`, `friend_count`, `treat_solution`, `member_type`) values ("
				+ ValueTransfer.SqlValueFor(getID())
				+ ","
				+ ValueTransfer.SqlValueFor(post_count)
				+ ","
				+ ValueTransfer.SqlValueFor(topic_count)
				+ ","
				+ ValueTransfer.SqlValueFor(integral)
				+ ","
				+ ValueTransfer.SqlValueFor(money)
				+ ","
				+ ValueTransfer.SqlValueFor(authority)
				+ ","
				+ ValueTransfer.SqlValueFor(experience)
				+ ","
				+ ValueTransfer.SqlValueFor(friend_count)
				+ ","
				+ ValueTransfer.SqlValueFor(treat_solution)
				+ ","
				+ ValueTransfer.SqlValueFor(member_type)
				+ ")";
		
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
	public void setUrl(String _url) {
		super.setUrl(_url);
		if (user_ID == null)
			user_ID = super.getID();
	}

	@Override
	public BigInteger getID() {
		if (user_ID == null) {
			user_ID = super.getID();
		}
		return user_ID;
	}

}
