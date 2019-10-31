package platform.db.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;


public class User39doctorDAO extends UserDAO {
	
	@Override
	public String getTableName() { return "user_39doctor"; }

	public BigInteger user_ID; // 唯一对应user表ID
	public String major;
	public String blog_page;
	public String answer_page;
	public int satisfy_degree;
	public int response_degree;
	public int help_count;
	public int accept_count;
	public int invited_count;
	public int star_count;
	public String type;
	public String hospital;
	public String title;
	public String introduction;
	public String homepage;

	public User39doctorDAO() {
		super();
		super.extend_table = new String("user_39doctor");
		user_ID = null;
		major = null;
		blog_page = null;
		answer_page = null;
		type = "其他";
		hospital = null;
		title = null;
		introduction = null;
		homepage = null;
		satisfy_degree = -1;
		response_degree = -1;
		help_count = -1;
		accept_count = -1;
		invited_count = -1;
		star_count = -1;

	}

	public void setDoctorInfo(String _major, String _blog_page,
			String _answer_page, String _type) {
		major = _major;
		blog_page = _blog_page;
		answer_page = _answer_page;
		type = _type;
	}

	public void setFamousDoctorInfo(String _hospital, String _title,
			String _introduction, String _homepage) {
		hospital = _hospital;
		title = _title;
		introduction = _introduction;
		homepage = _homepage;
	}

	public void setFormalDoctorInfo(int _satisfy_degree, int _response_degree,
			int _help_count, int _accept_count, int _invited_count,
			int _star_count) {
		satisfy_degree = _satisfy_degree;
		response_degree = _response_degree;
		help_count = _help_count;
		accept_count = _accept_count;
		invited_count = _invited_count;
		star_count = _star_count;
	}

	@Override
	public String[] insert() {
		String[] command = new String[2];
		command[0] = super.insert()[0];

		command[1] = "insert into `user_39doctor` (`user_ID`, `major`, `blog_page`, `answer_page`, `type`, `hospital`, `title`, `introduction`, `homepage`, `satisfy_degree`, `response_degree`, `help_count`, `accept_count`, `invited_count`, `star_count`) values ("
				+ ValueTransfer.SqlValueFor(getID())
				+ ","
				+ ValueTransfer.SqlValueFor(major)
				+ ","
				+ ValueTransfer.SqlValueFor(blog_page)
				+ ","
				+ ValueTransfer.SqlValueFor(answer_page)
				+ ","
				+ ValueTransfer.SqlValueFor(type)
				+ ","
				+ ValueTransfer.SqlValueFor(hospital)
				+ ","
				+ ValueTransfer.SqlValueFor(title)
				+ ","
				+ ValueTransfer.SqlValueFor(introduction)
				+ ","
				+ ValueTransfer.SqlValueFor(homepage)
				+ ","
				+ ValueTransfer.SqlValueFor(satisfy_degree)
				+ ","
				+ ValueTransfer.SqlValueFor(response_degree)
				+ ","
				+ ValueTransfer.SqlValueFor(help_count)
				+ ","
				+ ValueTransfer.SqlValueFor(accept_count)
				+ ","
				+ ValueTransfer.SqlValueFor(invited_count)
				+ ","
				+ ValueTransfer.SqlValueFor(star_count) + ")";
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
