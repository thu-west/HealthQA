
package platform.util.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import platform.util.log.Trace;

public class MysqlConnection {
	DBConfig config;
	
	private Connection conn = null;
	
	static Trace trace = new Trace().setValid(false, false);
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			trace.debug("data base add succeed");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Statement createStatement()
	{
		Statement s = null;
		try{
			s = conn.createStatement();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return s;
	}
	
	public MysqlConnection(DBConfig conf)
	{
		config = conf;
	}
	
	public PreparedStatement prepareStatement(String arg) throws SQLException
	{
		return conn.prepareStatement(arg);
	}
	
	// 连接数据库
	public Connection connect() {
		java.util.Properties prop = new java.util.Properties();
		prop.put("charSet", "utf8");
		prop.put("user", config.username);
		prop.put("password", config.password);
		try {
			conn = DriverManager.getConnection(config.url, prop);
			if (conn != null) {
				trace.debug("data link succeed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void setAutoCommit(boolean b)
	{
		try{
			conn.setAutoCommit(b);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void excuteBatch(Statement st)
	{
		try{
			setAutoCommit(false);
			st.executeBatch();
			conn.commit();
			setAutoCommit(true);
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	// 插入一个记录
	public int update(String sqlinsert) {
		try {
			Statement st = conn.createStatement();
			int count = st.executeUpdate(sqlinsert);
			return count;
		} catch (SQLException e) {
			//System.out.println(sqlinsert);
			e.printStackTrace();
		}
		return -1;
	}
	
	public ResultSet query(String sqlquery){
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlquery);
			return rs;
		} catch (SQLException e) {
			//System.out.println(sqlquery);
			e.printStackTrace();
		}
		return null;
	}
	
	public void disconnect(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		/*
		 * test the return value of excuteBatch function
		 */
		DBConfig c = new DBConfig();
		c.url =  new String( "jdbc:mysql://127.0.0.1:3307/ishc_data" );
		c.username = new String( "root" );
		c.password = new String("123qwe");
		MysqlConnection con = new MysqlConnection(c);
		con.connect();
	}
}
