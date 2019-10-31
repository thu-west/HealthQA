package platform.db.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;


public class UserXywydoctorDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_xywydoctor"; }

	public BigInteger user_ID; // 唯一对应user表ID
	
	String title;
	String department;
	String major;
	String hospital;
	String introduction;
	String level;
	int best_answer;
	int help_count;
	int credit;
	int thanks;
	int fans_count;

	public UserXywydoctorDAO() {
		super();
		super.extend_table = new String("user_xywydoctor");
		user_ID = null;
		title = null;
		department = null;
		major = null;
		hospital = null;
		introduction = null;
		level = null;
		best_answer = -1;
		help_count = -1;
		credit = -1;
		thanks = -1;
		fans_count = -1;

	}

	public void setStatistic(String _level, int _best_answer, int _help_count, int _credit, int _thanks, int _fans_count){
		level = _level;
		best_answer = _best_answer;
		help_count = _help_count;
		credit = _credit;
		thanks = _thanks;
		fans_count = _fans_count;
	}
	
	public void setProfession(String _title, String _department, String _major, String _hospital, String _introduction) {
		title = _title;
		department = _department;
		major = _major;
		hospital = _hospital;
		introduction = _introduction;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];

		command[1] = "insert into `user_xywydoctor` (`user_ID`, `title`,`department`,`major`,`hospital`,`introduction`,`level`,`best_answer`,`help_count`,`credit`,`thanks`,`fans_count`) values ("
				+ ValueTransfer.SqlValueFor(getID())
				+ ","
				+ ValueTransfer.SqlValueFor(title)
				+ ","
				+ ValueTransfer.SqlValueFor(department)
				+ ","
				+ ValueTransfer.SqlValueFor(major)
				+ ","
				+ ValueTransfer.SqlValueFor(hospital)
				+ ","
				+ ValueTransfer.SqlValueFor(introduction)
				+ ","
				+ ValueTransfer.SqlValueFor(level)
				+ ","
				+ ValueTransfer.SqlValueFor(best_answer)
				+ ","
				+ ValueTransfer.SqlValueFor(help_count)
				+ ","
				+ ValueTransfer.SqlValueFor(credit)
				+ ","
				+ ValueTransfer.SqlValueFor(thanks)
				+ ","
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
