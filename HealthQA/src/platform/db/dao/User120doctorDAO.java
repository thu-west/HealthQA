package platform.db.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;


public class User120doctorDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_120doctor"; }

	public BigInteger user_ID; // 唯一对应user表ID
	public String hospital;
	public String title;
	public String introduction;
	public String major;
	public String department;
	public String type;
	public int reply_count;
	public int accept_count;
	public float accept_rate;
	public int good_count;
	public float satisfy_rate;

	public User120doctorDAO() {
		super();
		super.extend_table = new String("user_120doctor");
		hospital = null;
		title = null;
		introduction = null;
		major = null;
		department = null;
		type = null;
		reply_count = -1;
		accept_count = -1;
		accept_rate = -1;
		good_count = -1;
		satisfy_rate = -1;

	}

	public void setMeta(String _hospital, String _title, String _introduction, String _major, String _department, String _type) {
		hospital = _hospital;
		title = _title;
		introduction = _introduction;
		major = _major;
		department = _department;
		type = _type;
	}

	public void setStat(int _reply_count, int _accept_count, float _accept_rate, int _good_count, float _satisfy_rate) {
		reply_count = _reply_count;
		accept_count = _accept_count;
		accept_rate = _accept_rate;
		good_count = _good_count;
		satisfy_rate = _satisfy_rate;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];

		command[1] = "insert into `user_120doctor` (`user_ID`, `hospital`, `title`, `introduction`, `major`, `department`, `type`, `reply_count`, `accept_count`, `accept_rate`, `good_count`, `satisfy_rate`) values ("
				+ ValueTransfer.SqlValueFor(getID())
				+ ","
				+ ValueTransfer.SqlValueFor(hospital)
				+ ","
				+ ValueTransfer.SqlValueFor(title)
				+ ","
				+ ValueTransfer.SqlValueFor(introduction)
				+ ","
				+ ValueTransfer.SqlValueFor(major)
				+ ","
				+ ValueTransfer.SqlValueFor(department)
				+ ","
				+ ValueTransfer.SqlValueFor(type)
				+ ","
				+ ValueTransfer.SqlValueFor(reply_count)
				+ ","
				+ ValueTransfer.SqlValueFor(accept_count)
				+ ","
				+ ValueTransfer.SqlValueFor(accept_rate)
				+ ","
				+ ValueTransfer.SqlValueFor(good_count)
				+ ","
				+ ValueTransfer.SqlValueFor(satisfy_rate)
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
